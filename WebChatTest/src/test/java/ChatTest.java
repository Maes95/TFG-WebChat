import com.globex.app.Result;
import com.globex.app.JSONFile;
import com.globex.app.WebChatAplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globex.app.TestResultsServer;
import io.vertx.core.Handler;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public final class ChatTest {
    
    // CONSTANTS
    
    public static final int REPEAT_LIMIT = 10;
    public static final int NUM_MESSAGES = 500;
    public static final int TIME = 5000;
    public static final int EXTRA = 180000;
    
    
    // STATIC CONTEXT 

    private static WebChatAplication current_application = null;
    private static long total_avg_time = 0;
    private static Result currentResult;
    
    static{
        // Set up results server
        TestResultsServer.setUp();
    }
    
    // CLASS ATRIBUTTES
    
    private Vertx vertx;
    private final int users;
    private final int usersPerChat;
    private final int numChats;
    private final int totalMessagePerChat;
    private final AtomicLong times = new AtomicLong(0);
    private final AtomicInteger done = new AtomicInteger(0);
    private final AtomicInteger sentMessages = new AtomicInteger(0);

    private long start = 0;

    @Parameters
    public static Collection<Object[]> data() {
        
        JSONObject properties = JSONFile.parse(System.getProperty("user.dir")+"/src/main/resources/config.json");

        List<Object[]> params = new ArrayList<>();
        
        // Names of aplications which participate in the test
        ((JSONArray) properties.get("apps")).forEach((_app) -> {
            // Number of chat romms
            ((JSONArray) properties.get("chats")).forEach((chat) -> {
                JSONObject _chat = (JSONObject) chat;
                int _numChats = _chat.getInt("numChats");
                // Number of users in chat
                ((JSONArray) _chat.get("users")).forEach((_numUsers) -> {
                    Object[] o = { _numUsers, _numChats, _app };
                    params.add(o);
                });
            });
        });
        
        return params;
    }

    public ChatTest(int usersPerChat, int numChats, JSONObject app_config){
        if(current_application != null && !app_config.getString("name").equals(current_application.getAppName())){
            current_application.destroy();
            current_application = null;
        }
        if(current_application == null){
            current_application = new WebChatAplication(app_config);
            current_application.run();
        }
        this.usersPerChat = usersPerChat;
        this.numChats = numChats;
        this.users = usersPerChat * numChats;
        this.totalMessagePerChat = (NUM_MESSAGES*users) / numChats;
    }

    @Before
    public void before(TestContext context) {
      vertx = Vertx.vertx();
    }

    @After
    public void after(TestContext context) {
      vertx.close();
    }

    @Test
    public void test0(TestContext context) {
        System.out.println("-------------------------------------------------------");
        System.out.println("App: "+current_application.getAppName());
        System.out.println("Nº Chats: "+numChats);
        System.out.println("Nº Users per chat: "+usersPerChat);
        System.out.println("-------------------------------------------------------");
        ChatTest.currentResult = new Result(numChats, usersPerChat, current_application.getAppName(),REPEAT_LIMIT);
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
        JsonObject result = ChatTest.currentResult.toJson();
        System.out.println("-------------------------------------------------------");
        System.out.println("Average time: "+total_avg_time/REPEAT_LIMIT);
        System.out.println("Average cpu use: "+result.getValue("avgCpuUse"));
        System.out.println("Average memory use: "+result.getValue("avgMemoryUse"));
        TestResultsServer.sendResult(result);
        total_avg_time = 0;
        context.assertTrue(true);
    }

    public void test(TestContext context, int attempt){
        Async async = context.async();
        start = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < users; i++) {
            newclient(
                    // User name
                    "User" + Double.toString(Math.random()), 
                    // Chat name
                    "chat_"+(i%numChats), 
                    // Total messages
                    NUM_MESSAGES * usersPerChat * usersPerChat, 
                    // Async context to finish test
                    async,
                    // Nº of attempt
                    attempt
            );
        }
        
        if(!current_application.isRemote()){
            vertx.setPeriodic(1000, id -> {
                ChatTest.currentResult.addMetric(current_application.getMetrics());
            });
        }

    }


    public void newclient(final String name, String chatName, final long totalMessages, Async async, int attempt) {
        final AtomicInteger numberOfMessages = new AtomicInteger(0);

        vertx.createHttpClient().websocket(current_application.getPort(), current_application.getAddress(), "/chat", websocket -> {

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
                            finishTest(totalMessages, attempt, async);
                        }
                    }
                });
                
                // CONNECTION MESSAGE

                websocket.writeFinalTextFrame("{\"chat\":\""+chatName+"\",\"name\":\""+name+"\"}");

                //SENDER

                vertx.setTimer(2000, (Long arg0) -> {
                    if (start == 0) {
                        start = System.currentTimeMillis();
                    }
                    final long timerID = vertx.setPeriodic(TIME / NUM_MESSAGES, new Handler<Long>() {
                        int i = 0;

                        @Override
                        public void handle(Long arg0) {
                            if (i < NUM_MESSAGES) {
                                websocket.writeFinalTextFrame(
                                        "{\"name\":\""+name+"\","
                                        + "\"chat\":\""+chatName+"\","
                                        +"\"message\":\""+Integer.toString(sentMessages.getAndAdd(1))+"/"+System.currentTimeMillis()+"\"}");
                                i++;
                            }

                        }
                    });
                });
        });
    }
    
    private final AtomicBoolean b = new AtomicBoolean(true);
    
    public synchronized void finishTest(long totalMessages, int attempt, Async async){
        if(b.get()){
            b.getAndSet(false);
            long avg_time = times.get()/totalMessages;
            ChatTest.currentResult.addTime(avg_time);
            System.out.println("Attempt "+attempt+":");
            System.out.println(" -> TIME: "+avg_time+" ms");
            total_avg_time += avg_time;
            async.complete();
        }
    }

}
