package com.globex.app;

// Vertx libraries
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author michel
 */

public class ChatManager extends AbstractVerticle {

    private final static int PORT = 9000;

    private final static String DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"Ya existe un usuario con ese nombre\"}";

    // The key is the chat name, that have many users
    private final ConcurrentHashMap<String, Map<String, User>> users = new ConcurrentHashMap<>();

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

                    if (!message.containsKey("message")){ 
                        if (userExist(message.getString("name"))){
                            //If the username exists it send a message and close the conexion
                            ws.writeFinalTextFrame(DUPLICATE_MSG);
                            ws.close();
                        }else{ 
                            // Create new user
                            newUser(message, ws);
                        }

                    }else{
                        users.get(message.getString("chat")).values().forEach((user)->{
                            user.send(message);
                        });
                    }
                });
            }else{
               ws.reject();
            }
        })
        .requestHandler((HttpServerRequest req) -> {
            if (req.uri().equals("/")) 
                req.response().sendFile("webroot/index.html"); // Serve the html
        }).listen(PORT);

    }
    
    private boolean userExist(String user_name){
        return users.values().stream().anyMatch((chat) -> (
            chat.containsKey(user_name)
        ));
    }

    private void newUser(JsonObject message, ServerWebSocket ws){
        String name = message.getString("name");
        String chat = message.getString("chat");
        User user = new User(name, chat, ws, this);
        if(users.containsKey(chat)){
            // Chat exist
            users.get(chat).put(name, user);
        }else{
            // Chat doesn't exist
            users.put(chat, new HashMap<>());
        }
    }

    //Remove the verticle and unregister the handler
    public void deleteUser (String chat_name, String name){
        users.get(chat_name).remove(name);
    }

}
