/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;

/**
 *
 * @author michel
 */
public class TestChatManager {
  
  public static void main(String[] args) {
    
    Runner.runExample(ChatManager2.class);
    
    // CLIENT SIDE
            
    HttpClient client = Vertx.vertx().createHttpClient();

    client.websocket(8080, "localhost", "/chat", websocket -> {
        websocket.handler(data -> {
            System.out.println("Server message: ");
            System.out.println("Received data " + data.toString("ISO-8859-1"));
        });
        websocket.writeBinaryMessage(Buffer.buffer("Hello server"));
    });
  }

}
