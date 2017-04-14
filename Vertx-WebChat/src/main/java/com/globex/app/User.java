package com.globex.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.ServerWebSocket;

/**
 *
 * @author michel
 */

public class User extends AbstractVerticle{
	
	private final String name;
	private final String chat;
        private final ServerWebSocket wss;
	
	public User(String name, String chat, ServerWebSocket wss) {
            this.name = name;
            this.chat = chat;
            this.wss = wss;
	}
	
        @Override
	public void start(){
            // Listen for messages from his chat
            vertx.eventBus().consumer(chat).handler( data -> {
                try{
                    // Try to send the message
                    this.wss.writeFinalTextFrame(data.body().toString());
                }catch(IllegalStateException e){
                    // The user is offline, so I delete it.
                    vertx.eventBus().publish("delete.user", name);
                    wss.close();
                } 
            });
	}
}