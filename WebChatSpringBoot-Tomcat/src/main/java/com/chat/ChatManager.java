package com.chat;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatManager {
	
	private static Map<String, Session> users = Collections.synchronizedMap(new HashMap<>());
	
	@OnOpen
    public void open(Session session) {
		synchronized (users) {
			users.put(session.getId(),session);
		}
	}

	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {
		// Broadcast message
		synchronized (users) {
			for(Session s: users.values()){
				s.getBasicRemote().sendText(message);
			}
		}
	}
	
	@OnClose
    public void close(Session session){
		synchronized (users) {
			users.remove(session.getId());
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
		System.err.println("BROKEN PIPE");
	}
		
}

