/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;

/**
 *
 * @author michel
 */
public class TestChatManager {
  
  public static void main(String[] args) {
    
    // SERVER SIDE
    
    HttpServer server = Vertx.vertx().createHttpServer();
    
    server.websocketHandler((ServerWebSocket webs) -> {
        System.out.println("Client connected");
        webs.writeBinaryMessage(Buffer.buffer("Hello user"));
        System.out.println("Client's message: ");
        webs.handler(data -> {System.out.println("Received data " + data.toString("ISO-8859-1"));});
    });

    server.listen(8080, "localhost", res -> {
        if (res.succeeded()) {
            System.out.println("Server is now listening!");
        } else {
            System.out.println("Failed to bind!");
        }
    });    
    
    // CLIENT SIDE
            
    HttpClient client = Vertx.vertx().createHttpClient();

    client.websocket(8080, "localhost", "/eventbus/", websocket -> {
        websocket.handler(data -> {
            System.out.println("Server message: ");
            System.out.println("Received data " + data.toString("ISO-8859-1"));
        });
        websocket.writeBinaryMessage(Buffer.buffer("Hello server"));
    });
  }

}
