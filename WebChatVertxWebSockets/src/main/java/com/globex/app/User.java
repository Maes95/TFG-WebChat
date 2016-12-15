/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author michel
 */

public class User extends AbstractVerticle{
	
	String name;
	String chat;
	
	public User(String name, String chat) {
            this.name = name;
            this.chat = chat;
	}
	
        @Override
	public void start(){
            vertx.eventBus().consumer(chat).handler( data -> {
                
                JsonObject message = (JsonObject) data.body();
                JsonObject respuesta = new JsonObject();
                respuesta.put("name", message.getString("user"));
                respuesta.put("color", message.getString("color"));
                respuesta.put("message", message.getString("message"));
                respuesta.put("sender", name);

                vertx.eventBus().send(name, respuesta);
            });
	}
}