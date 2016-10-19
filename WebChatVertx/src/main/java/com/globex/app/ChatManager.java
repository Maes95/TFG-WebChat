package com.globex.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.HashMap;

public class ChatManager extends Verticle {
	
	private ObjectMapper mapper = new ObjectMapper();
	private HashMap<String, String> users = new HashMap<String, String>(); //Add because I need the chat to send through the bus and normal messages haven't chat tag
	private HashMap<String, String> depID = new HashMap<String, String>(); //Add because the most easy way to obtain the deploymentID is when I deploy the verticle
	private HashMap<String, String> colors = new HashMap<String, String>();
	private String[] colorsArray = { "007AFF", "FF7000", "15E25F", "CFC700", "CFC700", "CF1100", "CF00BE", "F00" };
	private volatile int colorIndex = 0;
	Logger logger;
	

public void start() {
	  
      //logger = container.logger();
      vertx.eventBus().registerHandler("user??", newUserQueryHandler());
	  
      vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {
      public void handle(final ServerWebSocket ws) {
        if (ws.path().equals("/chat")) {
          ws.dataHandler(new Handler<Buffer>() {
            public void handle(Buffer data) {
            	JsonNode message= null;
            	try {
			message= mapper.readTree(data.getBytes());  //message to Json
		} catch (IOException e) {
			e.printStackTrace();
		}
            	
            	if (!message.has("message")){ //Is the log in message (don't have message) 
            		//If don't exists this username or a chat with the same name...
            		if ((!users.containsKey(message.get("user").asText()))&&(!users.containsValue(message.get("user").asText()))
            				&&((!message.get("user").asText().equals(message.get("chat").asText())))&&(!message.get("user").asText().contains("?"))){ 
            			JsonObject config = newUser(message);
                		container.deployVerticle("com/globex/app/User.java", config, newVerticleUser(ws,config));
                		
            		}else{ //If the username exists it send a message and close the conexion         			
        	    		ws.writeTextFrame(newMessageDuplicatedUser());
            			ws.close();  
            		}
            			
            	}else{ //Is a normal message. It sends to the eventBus with the chat like label
            		JsonObject msg = new JsonObject();
            		msg.putString("user", message.get("user").asText());
            		msg.putString("message", message.get("message").asText());
            		msg.putString("color", colors.get(message.get("user").asText())); 
            		vertx.eventBus().publish(users.get(message.get("user").asText()), msg);
            	}
            }
          });
        } else {
          ws.reject();
        }
      }
    }).requestHandler(new Handler<HttpServerRequest>() {
      
    public void handle(HttpServerRequest req) {
        if (req.path().equals("/")) req.response().sendFile("com/globex/resources/index.html"); // Serve the html
      }
    }).listen(9000);
  }
  
  
  //Create the handler who add the user to the maps and ask to the rest of nodes when the verticle is deployed
  private AsyncResultHandler<String> newVerticleUser(final ServerWebSocket ws, final JsonObject config){
  
  AsyncResultHandler<String> handler = new AsyncResultHandler<String>() {
      public void handle(AsyncResult<String> asyncResult) {
          if (asyncResult.succeeded()) {
        	depID.put(config.getString("name"), asyncResult.result()); //Save the deploymentID to later remove the verticle
//              logger.info("ADD : "+config.getString("name")+" WITH DepID: "+depID.get(config.getString("name")));
              
            //A new handler to send the user messages through the websocket
      		Handler<Message<String>> userHandler = newUserHandler(ws, config.getString("name"));
      		vertx.eventBus().registerHandler(config.getString("name"), userHandler);
      		AskNodesForUser(config.getString("name"),ws, userHandler);
          } else {
              	asyncResult.cause().printStackTrace();
          }
      }
      };
      return handler;
  }
  
  
  //Create a JSON with the configuration and add the user to the users and colors maps
  private JsonObject newUser (JsonNode message){
	JsonObject config = new JsonObject();								 
  	config.putString("name", message.get("user").asText());
	config.putString("chat", message.get("chat").asText());
	users.put(message.get("user").asText(), message.get("chat").asText()); 
	colors.put(message.get("user").asText(), colorsArray[colorIndex]); 
	colorIndex = (int) ((Math.random()*100) % colorsArray.length);
	return config;
  }
  
  
  //Create the handler that sends the user messages through the websocket
  private Handler<Message<String>> newUserHandler (final ServerWebSocket ws, final String user){
  	Handler<Message<String>> userHandler = new Handler<Message<String>>() {
      		public void handle(Message<String> message) {
			try{//Try to send the message
				ws.writeTextFrame(message.body());
			}catch(IllegalStateException e){ //The user is offline, so I delete it.
				deleteUser(user, this, ws);
			}
      		}
    	};  
    return userHandler;
  }

  //Create the duplicatedUser message
  private String newMessageDuplicatedUser(){
	ObjectNode msg = mapper.createObjectNode();
	msg.put("type", "system");
	msg.put("message", "Ya existe un usuario con ese nombre");
	return msg.toString();
  }


	//Remove the verticle and unregister the handler
  private void deleteUser (String user, Handler<Message<String>> handler,final ServerWebSocket ws){
//   logger.info("DELETING: "+ user +" WITH DepID: "+depID.get(user));
	container.undeployVerticle(depID.get(user));
	colors.remove(user);
	users.remove(user);
	depID.remove(user);
	vertx.eventBus().unregisterHandler(user, handler);
	try{
		ws.close();
	}catch(IllegalStateException e){}
  }


  //Returns a new handler that responds if the verticle have this user in its maps
  private Handler<Message<JsonObject>> newUserQueryHandler (){
  	Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>(){
		public void handle(Message<JsonObject> arg0) {
//			logger.info("ARRIVED QUESTION: "+arg0.body().getString("user")+" TO "+vertx.currentContext().toString()+" DESDE "+ arg0.body().getString("context"));
			if ((users.containsKey(arg0.body().getString("user")))&&(!vertx.currentContext().toString().equals(arg0.body().getString("context")))){
				vertx.eventBus().publish(arg0.body().getString("user")+"?", arg0.body().getString("context")); //I send the context because if there are 2 users with the same name there are 2 handlers
//				logger.info("SEND DELETE: "+arg0.body().getString("user")+" FROM "+vertx.currentContext().toString()+" A "+arg0.body().getString("context"));
			}		
		}
	};
	return handler;
  }
  
  
  //Ask to the rest of verticles if anyone have this user. If anyone reply with the label usernme+"?", it remove the user and close the websocket
  private void AskNodesForUser(final String user, final ServerWebSocket ws, final Handler<Message<String>> handler) {

	final Handler<Message<String>> replyHandler = new Handler<Message<String>>(){
		public void handle(Message<String> arg0) {
//				logger.info("RECIVED DELETE FOR "+arg0.body()+" IN "+vertx.currentContext());
			if (arg0.body().equals(vertx.currentContext().toString())){ //Only if I sent the query I remove the user
				  ws.writeTextFrame(newMessageDuplicatedUser());
				  deleteUser(user,handler,ws);
				  vertx.eventBus().unregisterHandler(user+"?", this);
			}
		}
	};

	vertx.eventBus().registerHandler(user+"?", replyHandler,new AsyncResultHandler<Void>(){  //This handler will receive a message when the handler is suscribed to the eventBus
		public void handle(AsyncResult<Void> arg0) {
//		          logger.info(arg0.toString());
			  JsonObject msg = new JsonObject();
			  msg.putString("user", user);
			  msg.putString("context", vertx.currentContext().toString());
//			  logger.info("SEND QUESTION: "+user+" FROM "+vertx.currentContext().toString());
			  vertx.eventBus().publish("user??", msg);
		}

	});

	vertx.setTimer(10000, new Handler<Long>() {
		public void handle(Long arg0) {
			vertx.eventBus().unregisterHandler(user+"?",replyHandler);
		}
	});
  }
  

}
