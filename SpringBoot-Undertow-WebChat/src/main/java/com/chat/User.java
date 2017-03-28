package com.chat;

import java.io.IOException;

import javax.websocket.Session;

public class User {
	private String name;
	private String chat;
	private final Session session;
	
	public User(Session session) {
                this.session = session;
	}
	
	public void setUp(String name, String chat) {
                this.name = name;
                this.chat = chat;
	}
	
	public synchronized void send(String message){
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
                    System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void send(String message, String chatName){
		if(this.chat.equals(chatName)){
			this.send(message);
		}
	}
	
	public String getChat(){
		return this.chat;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean isValid(){
		return this.name != null && this.chat != null;
	}


}
