package com.chat;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint("/chat")
public class ChatManager {
	
	private static Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
	private static final String DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"Ya existe un usuario con ese nombre\"}";
	
	private User user;
	
	@OnOpen
    public void open(Session session) {
		this.user = new User(session);
		System.out.println("New user");
	}

	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {

		if(this.user.isValid()){
			// Broadcast message
			synchronized (users) {
				users.values().forEach( user -> user.send(message, this.user.getChat()) );
			}
		}else{
			// Connect message
			newUser(message);
		}

	}
	
	@OnClose
    public void close(Session session){
		synchronized (users) {
			users.remove(this.user.getName());
		}
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
			synchronized (users) {
				users.put(name, this.user);
			}
		}
	}
}

