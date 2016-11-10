package com.globex.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

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
        EventBus eb = vertx.eventBus();
        
        // User is subscribe to his chat events
        
        eb.consumer(chat).handler(message -> {
            System.out.println("Le llegó un mensaje a: "+name+" -> "+message.body());
        });
    }
}
