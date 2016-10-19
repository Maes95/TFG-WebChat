package com.globex.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class User extends Verticle{
	
	String name;
//	String color;
	String chat;
	private ObjectMapper mapper;
	
	public User() {
		// TODO Auto-generated constructor stub
		mapper = new ObjectMapper();
	}
	
	public void start(){
		this.name = container.config().getString("name");
//		this.color = container.config().getString("color");
		this.chat = container.config().getString("chat");
		
		vertx.eventBus().registerHandler(chat, new Handler<Message<JsonObject>>() {
		      public void handle(Message<JsonObject> message) {
		        //JsonObject respuesta = new JsonObject();
//		        respuesta.putString("type", "NoSystem");
		        //respuesta.putString("name", message.body().getString("user"));
		        //respuesta.putString("color", message.body().getString("color"));
		        //respuesta.putString("message", message.body().getString("message"));
		        //respuesta.putString("sender", name);
				  ObjectNode msg = mapper.createObjectNode();
				  msg.put("type", "NoSystem");
				  msg.put("name", message.body().getString("user"));
				  msg.put("color", message.body().getString("color"));
				  msg.put("message", message.body().getString("message"));

		        
		        vertx.eventBus().send(name, msg.toString());
		      }
		    });
	}
}
