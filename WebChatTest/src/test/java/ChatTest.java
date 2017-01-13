import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globex.app.ChatTestResultsServer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public final class ChatTest {

    public static final long REPEAT_LIMIT = 10;
    private static final int NUM_MESSAGES = 500;
    private static final int TIME = 5000;
    private static final int EXTRA = 180000;
    private final static int PORT = 9000;

    private static long total_avg_time = 0;

    Vertx vertx;
    int users;
    int usersPerChat;
    int numChats;
    int totalMessagePerChat;

    AtomicLong times = new AtomicLong(0);
    AtomicInteger done = new AtomicInteger(0);
    AtomicInteger sentMessages = new AtomicInteger(0);

    long start = 0;

    public static ArrayList<Result> results = new ArrayList<>();

    private static final Result currentResult = new Result();

     @Parameters
     public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
                  // N users / 1 chat room
                  { 10, 1, "Node" }, { 20, 1, "Node" }, { 30, 1, "Node" },
                  { 40, 1, "Node" }, { 50, 1, "Node" }, { 60, 1, "Node" },
                  // N users / 2 chat rooms
                  { 20, 2, "Node" }, { 25, 2, "Node" }, { 30, 2, "Node" }, { 35, 2, "Node" },
                  // N users / 4 chat rooms
                  { 10, 4, "Node" }, { 12, 4, "Node" }, { 15, 4, "Node" }, { 17, 4, "Node" },
                  
                  // N users / 1 chat room
                  { 10, 1, "Vertx" }, { 20, 1, "Vertx" }, { 30, 1, "Vertx" },
                  { 40, 1, "Vertx" }, { 50, 1, "Vertx" }, { 60, 1, "Vertx" },
                  // N users / 2 chat rooms
                  { 20, 2, "Vertx" }, { 25, 2, "Vertx" }, { 30, 2, "Vertx" }, { 35, 2, "Vertx" },
                  // N users / 4 chat rooms
                  { 10, 4, "Vertx" }, { 12, 4, "Vertx" }, { 15, 4, "Vertx" }, { 17, 4, "Vertx" },
         });
     }

     public static Process process = null;
     public static String app = null;


     public ChatTest(int usersPerChat, int numChats, String newApp){
         if(app != null && !newApp.equals(app)){
             process.destroy();
             process = null;
         }
         app = newApp;
         if(process == null){
             this.newServer();
         }
        this.usersPerChat = usersPerChat;
        this.numChats = numChats;
        this.users = usersPerChat * numChats;
        this.totalMessagePerChat = (NUM_MESSAGES*users) / numChats;
    }

     public void newServer(){
         System.out.println("New server");
         switch(app){
             case "Node": runServerSH("WebChatNodeWebsockets");
                 break;
             case "Akka": runServerSH("WebChatAkkaPlay");
                 break;
             case "Vertx": runServerSH("WebChatVertxWebSockets");
                 break;
         }
     }
    
     private static final String path = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - 11);
    
     public void runServerSH(String folderName){
         try {
             System.out.println(app);
             process = new ProcessBuilder("./run.sh").directory(new File(path+folderName)).start();
             Thread.sleep(2000);
         } catch (IOException ex) {
             Logger.getLogger(ChatTest.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InterruptedException ex) {
             Logger.getLogger(ChatTest.class.getName()).log(Level.SEVERE, null, ex);
         }
     }


    @Before
    public void before(TestContext context) {
      vertx = Vertx.vertx();
    }

    @After
    public void after(TestContext context) {
      vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test0(TestContext context) {
        System.out.println("-------------------------------------------------------");
        System.out.println("Nº Chats: "+numChats);
        System.out.print("Nº Users per chat: "+usersPerChat);
        ChatTest.currentResult.setUp(numChats, usersPerChat, app);
        ChatTestResultsServer.setUp();
        test(context, 1);
    }

    @Test
    public void test1(TestContext context) {
        test(context, 2);
    }

    @Test
    public void test2(TestContext context) {
        test(context, 3);
    }

    @Test
    public void test3(TestContext context) {
        test(context, 4);
    }

    @Test
    public void test4(TestContext context) {
        test(context, 5);
    }

    @Test
    public void test5(TestContext context) {
        test(context, 6);
    }

    @Test
    public void test6(TestContext context) {
        test(context, 7);
    }

    @Test
    public void test7(TestContext context) {
        test(context, 8);
    }

    @Test
    public void test8(TestContext context) {
        test(context, 9);
    }

    @Test
    public void test9(TestContext context) {
        test(context, 10);
    }

    @Test
    public void testZ(TestContext context) {
        System.out.println("\nAverage time: "+total_avg_time/REPEAT_LIMIT);
        ChatTestResultsServer.sendResult(ChatTest.currentResult.toJson());
        total_avg_time = 0;
        context.assertTrue(true);
    }

    public void test(TestContext context, int attempt) {
        System.out.println("");
        System.out.print("Attempt "+attempt+": ");
        Async async = context.async();
        start = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < users; i++) {
            String chatName = "chat_"+(i%numChats);
            newclient("User" + Double.toString(Math.random()), TIME + EXTRA, NUM_MESSAGES * usersPerChat * usersPerChat, NUM_MESSAGES, TIME, async,chatName);
        }
    }


    public void newclient(final String name, final long totalTime, final long totalMessages, final long messages, final long sendTime, Async async, String chatName) {
        final AtomicInteger numberOfMessages = new AtomicInteger(0);

        vertx.createHttpClient().websocket(PORT, "localhost", "/chat", websocket -> {

                websocket.handler(data -> {
                    JsonNode message = null;
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        message = mapper.readTree(data.getBytes());  //message to Json
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String respuesta = message.get("message").asText();
                    Long _time = System.currentTimeMillis()-Long.parseLong(respuesta.substring(respuesta.indexOf("/") + 1));
                    times.addAndGet(_time);
                    numberOfMessages.addAndGet(1);
                    // When THIS user recive all messages from his chat
                    if (numberOfMessages.get()== totalMessagePerChat){
                        websocket.close();
                        done.addAndGet(1);
                        // When ALL users recive all messages
                        if (done.get()==users){
                            long avg_time = times.get()/totalMessages;
                            ChatTest.currentResult.addTime(avg_time);
                            System.out.print(avg_time);
                            total_avg_time += avg_time;
                            async.complete();
                        }
                    }
                });

                // CONNECTION MESSAGE
                JsonObject json = new JsonObject();
                json.put("chat", chatName);
                json.put("user", name);
                websocket.writeFinalTextFrame(json.toString());

                //SENDER

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

                // TIME-OUT

//                vertx.setTimer(totalTime, (Long arg0) -> {
//                    System.out.println("TIME OUT");
//                    System.out.println("NUMBER OF MESSAGES:" + numberOfMessages.get());
//                    websocket.close();
//                    done.addAndGet(1);
//                    if (done.get()==users){
//                        async.complete();
//                    }
//                });

        });
    }

}
