package com.globex.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

public class ChatManagerSimple extends Verticle {

	private ObjectMapper mapper = new ObjectMapper();
	private HashMap<String, String> users = new HashMap<String, String>(); //Add because I need the chat to send through the bus and normal messages haven't chat tag
	private HashMap<String, String> colors = new HashMap<String, String>();
	private String[] colorsArray = { "007AFF", "FF7000", "15E25F", "CFC700", "CFC700", "CF1100", "CF00BE", "F00" };
	private volatile int colorIndex = 0;
	Logger logger;


	public void start() {

		logger = container.logger();

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
										&&((!message.get("user").asText().equals(message.get("chat").asText())))){
									newUser(ws, message);
									//logger.info("newUser");
									//container.deployVerticle("com/globex/app/User.java", config, newVerticleUser(ws,config));

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
								//logger.info("ENVIADO MENSAJE" + users.get(message.get("user").asText())+msg.toString());
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

	//Create the duplicatedUser message
	private String newMessageDuplicatedUser(){
		ObjectNode msg = mapper.createObjectNode();
		msg.put("type", "system");
		msg.put("message", "Ya existe un usuario con ese nombre");
		return msg.toString();
	}

	//Create a JSON with the configuration and add the user to the users and colors maps
	private void newUser (final ServerWebSocket ws, JsonNode message){
		final String user = message.get("user").asText();
		JsonObject config = new JsonObject();
		config.putString("name", user);
		config.putString("chat", message.get("chat").asText());
		users.put(user, message.get("chat").asText());
		colors.put(user, colorsArray[colorIndex]);
		colorIndex = (int) ((Math.random()*100) % colorsArray.length);


		vertx.eventBus().registerHandler(message.get("chat").asText(), newUserHandler(ws, user));
	}


	//Remove the verticle and unregister the handler
	private void deleteUser (String user, Handler<Message<JsonObject>> handler,final ServerWebSocket ws) {
		colors.remove(user);
		users.remove(user);
		vertx.eventBus().unregisterHandler(user, handler);
		try {//Try to send the message
			ws.close();
		} catch (IllegalStateException e) {}

	}


	//Create the handler that sends the user messages through the websocket
	private Handler<Message<JsonObject>> newUserHandler (final ServerWebSocket ws, final String user){
		Handler<Message<JsonObject>> userHandler = new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> message) {
				//logger.info("RECIBIDO MENSAJE");
				ObjectNode msg = mapper.createObjectNode();
				msg.put("type", "NoSystem");
				msg.put("name", message.body().getString("user"));
				msg.put("color", message.body().getString("color"));
				msg.put("message", message.body().getString("message"));
				//sendMessage(ws, msg, user, this);
				try {//Try to send the message
					ws.writeTextFrame(msg.toString());
				} catch (IllegalStateException e) { //The user is offline, so I delete it.
					deleteUser(user, this, ws);
				}
			}
		};
		return userHandler;
	}

}
