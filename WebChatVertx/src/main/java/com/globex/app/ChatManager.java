package com.globex.app;

// Vertx libraries
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.json.JsonObject;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

/**
 * A verticle which implements a simple, realtime,
 * multiuser chat. Anyone can connect to the chat application on port
 * 8080 and type messages. The messages will be rebroadcast to all
 * connected users via the Websocket bridge.
 */

public class ChatManager extends AbstractVerticle {

  private final HashMap<String, String> users = new HashMap<>();

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(ChatManager.class);
  }

  @Override
  @SuppressWarnings("empty-statement")
  public void start() throws Exception {

    Router router = Router.router(vertx);

    // Allow events for the designated addresses in/out of the event bus bridge
    BridgeOptions opts = new BridgeOptions()
      // CLIENT TO SERVER
      .addInboundPermitted(new PermittedOptions().setAddressRegex("(.)*"))
      // SERVER TO CLIENT
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
      // Send the message back out to all clients with the timestamp prepended.
      JsonObject newMessage = processMessage(message);
      eb.publish(newMessage.getString("chat"), newMessage);
    });

    // Register to listen for new client conection
    eb.consumer("connect").handler(message -> {
      String user_name = (String) message.body();
      if(users.get(user_name) == null){
        // Sends true to client if user_name does not exist
        eb.send(message.replyAddress(), true);
        users.put(user_name, user_name);
      }else{
        // Sends true to client if user_name exist
        eb.send(message.replyAddress(), false);
      };
    });

  }

  public void logger(Object obj){
      System.out.println(obj);
  }

  public JsonObject processMessage(Message message){
      // Parse message body
      JsonObject newMessage = (JsonObject) message.body();
      // Create a timestamp string
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
      newMessage.put("timestamp", timestamp);
      logger(newMessage.getString("user")+" said: \""+newMessage.getString("text")+"\" to chatroom: "+newMessage.getString("chat"));
      return newMessage;
  }
}
