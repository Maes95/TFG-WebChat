package com.globex.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.core.Vertx;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChatTestClass {

    AtomicInteger msg = new AtomicInteger(0);
    AtomicLong times = new AtomicLong(0);
    //AtomicInteger msg2 = new AtomicInteger(0);
    AtomicInteger done = new AtomicInteger(0);
    long start = 0;
    AtomicInteger sentMessages = new AtomicInteger(0);
    String ip = "192.168.1.129";
    //String ip = "chatbalancer-1698952741.eu-west-1.elb.amazonaws.com";
    boolean akka = true;
    boolean haproxy = false;
    int users = 50;
    int messages = 500;
    int time = 5000;
    int extra =180000;
    String chatName = "chat" + Double.toString(Math.random());
    
    static Vertx vertx;
    
    public static void main(String[] args) {
        vertx = Vertx.vertx();
        ChatTestClass example = new ChatTestClass();
        for(int i = 0; i < 10; i++){
            example.test();
        }
//        example.test9();
    }


    public void test() {
        //System.out.println(chatName);
        start = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testClients(users, messages, time, extra + 1000);
        //testClientsMax(10, 4000, 10000);
    }

    public void testClients(int users, long messages, long time, int extra) {
        //listenerClientHalfTime("LUser"+Double.toString((Math.random()*100)), time + extra, totalMessages*2);
        //listenerClient("/////////////////////////////////////////////User" + Double.toString(Math.random()), time + extra, messages * users);
        for (int i = 0; i < users; i++) {
            //senderClient("user" + Double.toString(i + (Math.random() * 100)), (time / (totalMessages / users)), time);
            //senderClient("user" + Double.toString(i + Math.random()), time/messages, time);
            newclient2("User" + Double.toString(Math.random()), time + extra, messages * users * users, messages, time);
        }
    }

    public void newclient2(final String name, final long totalTime, final long totalMessages, final long messages, final long sendTime) {
        final Boolean[] recievedMessages = new Boolean[(int) messages*users];
        for (int e = 0; e < recievedMessages.length; e++){
            recievedMessages[e] = false;
        }
        final AtomicInteger numberOfMessages = new AtomicInteger(0);
        final AtomicBoolean auxDone = new AtomicBoolean(false);

        vertx.createHttpClient().websocket(8080, "localhost", "/chat", websocket -> {

                websocket.handler(data -> {
                    JsonNode message = null;
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        message = mapper.readTree(data.getBytes());  //message to Json
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String respuesta = message.get("message").asText();
                    recievedMessages[Integer.parseInt(respuesta.substring(0,respuesta.indexOf("/")))] = true;
                    times.addAndGet(System.currentTimeMillis()-Long.parseLong(respuesta.substring(respuesta.indexOf("/") + 1)));
                    msg.addAndGet(1);
                    //msg2.addAndGet(1);
                    numberOfMessages.addAndGet(1);
                    if (numberOfMessages.get()== messages*users){
                        Boolean ok = true;
                        //System.out.println("GLOBAL MESSAGES:"+msg.get());
                        //System.out.println("GLOBAL MESSAGES2:"+msg2.get());
                        //System.out.println("NUMBER OF MESSAGES:"+numberOfMessages.get());
                        for (int i = 0; i < recievedMessages.length; i++) {
                            if (recievedMessages[i] == false) {
                                System.out.println("Falta: " + Integer.toString(i));
                                ok = false;
                            }
                        }
                        websocket.close();
                        System.out.println("ok es: "+ok);
                        done.addAndGet(1);
                        if (done.get()==users){
                            System.out.println(msg.get() + "/" + totalMessages + "/" + Integer.toString(sentMessages.get()));
                            //long time = System.currentTimeMillis() - start;
                            long time = times.get()/totalMessages;
                            PrintWriter writer = null;
                            try {
                                writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            writer.append(Long.toString(time) + "\n");
                            writer.close();
                            System.out.println("Tiempo:  " + (System.currentTimeMillis() - start));
                            System.out.println("Tiempo medio: "+ times.get()/totalMessages);
                            System.out.println("Complete");
                        }
                    }
                });
                
                JsonObject json = new JsonObject();
                json.put("chat", chatName);
                json.put("user", name);
                websocket.writeFinalTextFrame(json.toString());

                //SENDER ADDED
                vertx.setTimer(2000, (Long arg0) -> {
                    if (start == 0) {
                        start = System.currentTimeMillis();
                    }
                    final long timerID = vertx.setPeriodic(sendTime / messages, new Handler<Long>() {
                        int i = 0;
                        
                        @Override
                        public void handle(Long arg0) {
                            if (i < messages) {
                                JsonObject json2 = new JsonObject();
                                json2.put("user", name);
                                json2.put("message", Integer.toString(sentMessages.getAndAdd(1))+"/"+System.currentTimeMillis());
                                websocket.writeFinalTextFrame(json2.toString());
                                i++;
                            }
                            
                        }
                    });
                });
                ////////////////////////////////////////////////////////

                vertx.setTimer(totalTime, (Long arg0) -> {
                    //System.out.println(msg.get() + "/"+msg2+"/" + totalMessages + "/" + Integer.toString(sentMessages.get()));
                    System.out.println("NUMBER OF MESSAGES:" + numberOfMessages.get());
                    System.out.println("GLOBAL MESSAGES:"+msg.get());
                    for (int i = 0; i < recievedMessages.length; i++) {
                        if (recievedMessages[i] == false) {
                            //System.out.println("Falta: " + Integer.toString(i));
                        }
                    }
                    websocket.close();
                    done.addAndGet(1);
                    if (done.get()==users){
                        System.out.println("Falló");
                    }
                });

        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void test9() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long result = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("results.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            line = br.readLine();

            while (line != null) {
                System.out.print(result + " + " + line);
                result += Long.parseLong(line);
                System.out.println(" = " + result);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result / 10);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("results.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.close();
        PrintWriter writer2 = null;
        try {
            writer2 = new PrintWriter(new FileOutputStream(new File("results2.txt"), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer2.append(Integer.toString(users) + " " + Long.toString(result / 10) + "\n");
        writer2.close();
        System.out.println("COMPLETE!");
    }

}