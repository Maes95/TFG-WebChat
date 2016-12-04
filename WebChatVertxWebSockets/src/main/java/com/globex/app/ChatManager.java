package com.globex.app;

// Vertx libraries
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import static io.vertx.core.json.Json.mapper;
import io.vertx.core.json.JsonObject;
import java.io.IOException;

import java.util.HashMap;

public class ChatManager extends AbstractVerticle {

  final static int port = 9000;

  //Add because I need the chat to send through the bus and normal messages haven't chat tag
  private final HashMap<String, String> users = new HashMap<>();
  //Add because the most easy way to obtain the deploymentID is when I deploy the verticle
  private final HashMap<String, String> depID = new HashMap<>();

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(ChatManager.class);
  }

  @Override
  @SuppressWarnings("empty-statement")
  public void start() throws Exception {

    HttpServer server = vertx.createHttpServer();

    server.websocketHandler( (ServerWebSocket ws) -> {
        if (ws.path().equals("/chat")) {
            ws.handler((Buffer data) -> {
                JsonNode message = parseJSON(data);

                if (!message.has("message")){ //Is the log in message (don't have message)
                    //If don't exists this username or a chat with the same name...
                    if ((!users.containsKey(message.get("user").asText())) &&((!message.get("user").asText().equals(message.get("chat").asText())))&&(!message.get("user").asText().contains("?"))){
                        // Create new user
                        newUser(message, ws);
                    }else{ //If the username exists it send a message and close the conexion
                        ws.writeFinalTextFrame(newMessageDuplicatedUser());
                        ws.close();
                    }

                }else{
                    //Is a normal message. It sends to the eventBus with the chat like label
                    JsonObject msg = new JsonObject();
                    msg.put("user", message.get("user").asText());
                    msg.put("message", message.get("message").asText());
                    String chatName = users.get(message.get("user").asText();
                    vertx.eventBus().publish(chatName, msg);
                }
            });
        }else{
           ws.reject();
        }
    })
    .requestHandler((HttpServerRequest req) -> {
        if (req.uri().equals("/")) req.response().sendFile("webroot/index.html"); // Serve the html
    }).listen(port);

  }


  //Create a new message to send through the websocket from the recived through the bus
  private String newMessage(JsonObject message){
        ObjectNode msg = mapper.createObjectNode();
        msg.put("type", "NoSystem");
        msg.put("name", message.getString("name"));
        msg.put("color", message.getString("color"));
        msg.put("message", message.getString("message"));
        return msg.toString();
  }

  public void newUser(JsonNode message, ServerWebSocket ws){
    String name = message.get("user").asText();
    String chat = message.get("chat").asText();
    User user = new User(name, chat);
    vertx.deployVerticle(user, res -> {
        if (res.succeeded()) {
          //Save the deploymentID to later remove the verticle
          depID.put(name, res.result());
          users.put(name, chat);
          //A new handler to send the user messages through the websocket
          vertx.eventBus().consumer(name).handler( msg -> {
              JsonObject _msg = (JsonObject) msg.body();
              try{
                 //Try to send the message
                 ws.writeFinalTextFrame(newMessage(_msg));
              }catch(IllegalStateException e){
                 //The user is offline, so I delete it.
                 deleteUser(_msg.getString("sender"));
              }
          });
        } else {
          System.err.println("Error at deploy User");
        }
    });
  }


  //Create the duplicatedUser message
  private String newMessageDuplicatedUser(){
        ObjectNode msg = mapper.createObjectNode();
        msg.put("type", "system");
        msg.put("message", "Ya existe un usuario con ese nombre");
        return msg.toString();
  }


  //Remove the verticle and unregister the handler
  private void deleteUser (String user){
        vertx.undeploy(depID.get(user), res -> {
            if (res.succeeded()) {
                System.out.println("Undeployed ok");
            } else {
                System.out.println("Undeploy failed!");
            }
        });
        users.remove(user);
        depID.remove(user);
        vertx.eventBus().consumer(user).unregister();
  }

  private JsonNode parseJSON(Buffer data){
    JsonNode message= null;
    try {
        message= mapper.readTree(data.getBytes());  //message to Json
    } catch (IOException e) {
        System.err.println(e);
    }
    return message;
  }

}
