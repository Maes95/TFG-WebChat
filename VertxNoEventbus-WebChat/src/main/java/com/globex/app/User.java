package com.globex.app;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author michel
 */

public class User{
	
	private final String name;
	private final String chat;
        private final ServerWebSocket wss;
        private final ChatManager manager;
	
	public User(String name, String chat, ServerWebSocket wss, ChatManager manager) {
            this.name = name;
            this.chat = chat;
            this.wss = wss;
            this.manager = manager;
	}
        
        public void send(JsonObject message){
            try{
                // Try to send the message
                this.wss.writeFinalTextFrame(message.toString());
            }catch(IllegalStateException e){
                // The user is offline, so I delete it.
                manager.deleteUser(chat, name);
            } 
        }
        
        public boolean equals(String name){
            return this.name.equals(name);
        }

}