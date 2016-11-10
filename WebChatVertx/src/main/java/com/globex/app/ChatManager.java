package com.globex.app;

// Vertx libraries
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.eventbus.impl.MessageImpl;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * A verticle which implements a simple, realtime,
 * multiuser chat. Anyone can connect to the chat application on port
 * 8080 and type messages. The messages will be rebroadcast to all
 * connected users via the Websocket bridge.
 */

public class ChatManager extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(ChatManager.class);
  }

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    // Allow events for the designated addresses in/out of the event bus bridge
    BridgeOptions opts = new BridgeOptions()
      // CLIENT TO SERVER
      // .addInboundPermitted(new PermittedOptions().setAddress("new.message"))
      // .addInboundPermitted(new PermittedOptions().setAddress("connect"))
      .addInboundPermitted(new PermittedOptions().setAddressRegex("(.)*"))
      // SERVER TO CLIENT (ALL)
      // .addOutboundPermitted(new PermittedOptions().setAddress("message"))
      .addOutboundPermitted(new PermittedOptions().setAddressRegex("(.)*"));

    // Create the event bus bridge and add it to the router.
    SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
    router.route("/eventbus/*").handler(ebHandler);

    // Create a router endpoint for the static content.
    router.route().handler(StaticHandler.create());

    // Start the web server and tell it to use the router to handle requests.
    vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    EventBus eb = vertx.eventBus();

    // Register to listen for messages coming IN to the server
    eb.consumer("new.message").handler(message -> {
      logger(message);
      // Create a timestamp string
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
      // Send the message back out to all clients with the timestamp prepended.
      eb.publish("message", timestamp + ": " + message.body());
    });

    eb.consumer("connect").handler(message -> {
        
      JsonObject newMessage = (JsonObject) message.body();
      String chat_name = newMessage.getString("chat");
      String user_name = newMessage.getString("user");
      logger("NEW USER CONNECTED: "+user_name+" to chat "+chat_name);
        
      User newUser = new User(user_name, chat_name);
        
      vertx.deployVerticle(newUser, res -> {
        if (res.succeeded()) {
          logger("SUCCEDED");
//          eb.consumer("connect").handler(_message -> {
//          
//          });
        } else {
          logger(res.cause());
        }
      });

      

    });

  }

  public void logger(Object obj){
    System.out.println(obj);
  }
}
