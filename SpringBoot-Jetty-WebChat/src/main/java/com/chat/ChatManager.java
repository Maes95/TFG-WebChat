package com.chat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint("/chat")
public class ChatManager {
	
    private static final Map<String, User> users = new ConcurrentHashMap<>();
    private static final String DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"Ya existe un usuario con ese nombre\"}";

    private User user;

    @OnOpen
    public void open(Session session) {
            this.user = new User(session);
    }

    @OnMessage
    public void handleMessage(Session session, String message) throws IOException {

            if(this.user.isValid()){
                // Broadcast message
                users.values().forEach( _user -> _user.send(message, this.user.getChat()) );
            }else{
                // Connect message
                newUser(message);
            }

    }

    @OnClose
    public void close(Session session){
        
            users.remove(this.user.getName());
            try {
                    session.close();
                    System.out.println("Sessión cerrada");
            } catch (IOException e) {
                    e.printStackTrace();
            }
            
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        
            System.out.println("Cliente "+session.getId()+" desconectado");
            System.err.println(thr.getMessage());
            thr.printStackTrace();
    
    }

    private void newUser(String message){
        
            JSONObject jsonMessage = new JSONObject(message);
            String chatName = jsonMessage.getString("chat");
            String name = jsonMessage.getString("name");
            if(users.containsKey(name)){
                    this.user.send(DUPLICATE_MSG);
            }else{
                    this.user.setUp(name, chatName);
                    users.put(name, this.user);
            }
    
    }
}

