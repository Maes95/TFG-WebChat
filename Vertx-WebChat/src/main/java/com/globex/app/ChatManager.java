package com.globex.app;

// Vertx libraries
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author michel
 */

public class ChatManager extends AbstractVerticle {

    private final static int PORT = 9000;

    private final static String DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"Ya existe un usuario con ese nombre\"}";

    //Add because I need the chat to send through the bus and normal messages haven't chat tag
    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    //Add because the most easy way to obtain the deploymentID is when I deploy the verticle
    private final ConcurrentHashMap<String, String> depID = new ConcurrentHashMap<>();

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
      Runner.runExample(ChatManager.class);
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void start() throws Exception {

        vertx.createHttpServer().websocketHandler( (ServerWebSocket ws) -> {
            if (ws.path().equals("/chat")) {
                ws.handler((Buffer data) -> {

                    JsonObject message = data.toJsonObject();

                    if (!message.containsKey("message")){ //Is the log in message (don't have message)
                        //If don't exists this username or a chat with the same name...
                        if (!users.containsKey(message.getString("name"))){
                            // Create new user
                            newUser(message, ws);
                        }else{ //If the username exists it send a message and close the conexion
                            ws.writeFinalTextFrame(DUPLICATE_MSG);
                            ws.close();
                        }

                    }else{
                        //Is a normal message. It sends to the eventBus with the chat like label
                        String chatName = users.get(message.getString("name"));
                        // Broadcast the message to all Users
                        vertx.eventBus().publish(chatName, message);
                    }
                });
            }else{
               ws.reject();
            }
        })
        .requestHandler((HttpServerRequest req) -> {
            if (req.uri().equals("/")) req.response().sendFile("webroot/index.html"); // Serve the html
        }).listen(PORT);
        
        // Listen for disconected users event
        vertx.eventBus().consumer("delete.user", data -> {
            this.deleteUser(data.body().toString());
        });

    }

    private void newUser(JsonObject message, ServerWebSocket ws){
        String name = message.getString("name");
        String chat = message.getString("chat");
        User user = new User(name, chat, ws);
        vertx.deployVerticle(user, res -> {
            if (res.succeeded()) {
                //Save the deploymentID to later remove the verticle
                depID.put(name, res.result());
                users.put(name, chat);
            } else {
                System.err.println("Error at deploy User");
            }
        });
    }

    //Remove the verticle and unregister the handler
    private void deleteUser (String user_name){
          vertx.undeploy(depID.get(user_name), res -> {
              if (res.succeeded()) {
                  System.out.println("Undeployed ok");
              } else {
                  System.err.println("Undeploy failed!");
              }
          });
          users.remove(user_name);
          depID.remove(user_name);
    }

}
