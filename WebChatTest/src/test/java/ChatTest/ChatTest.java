package ChatTest;

import client.Result;
import client.TestResultsServer;
import WebChatApp.WebChatApp;
import WebChatApp.WebChatAppFactory;
import WebChatApp.WebChatAppFactoryMethod;
import io.vertx.core.Handler;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utils.Colors;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ChatTest {
    
    // CONSTANTS
    
    public static final int REPEAT_LIMIT = 10;
    public static final int NUM_MESSAGES = 500;
    public static final int TIME = 5000;
    public static final int EXTRA = 180000;
    
    
    // STATIC CONTEXT 

    private static WebChatApp current_application = null;
    private static long total_avg_time = 0;
    private static Result currentResult;
    private static final WebChatAppFactoryMethod webChatAppFactory;
    
    static{
        // Create logs folder
        File logs = new File("logs");
        if(!logs.exists()) logs.mkdir();
        // Print header
        ChatTestUtils.printHeader();
        // Set up results server
        TestResultsServer.setUp();
        // To create web chat applications
        webChatAppFactory = new WebChatAppFactory();
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
        return ChatTestUtils.getConfig();
    }

    public ChatTest(int usersPerChat, int numChats, JSONObject app_config){
        if(current_application != null && !app_config.getString("name").equals(current_application.getAppName())){
            current_application.stop();
            current_application = null;
        }
        if(current_application == null){
            current_application = webChatAppFactory.createWebChatApp(app_config);
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
    
    @AfterClass 
    public static void close() {
        if(current_application != null) current_application.stop();
    }

    @Test
    public void aaInit(TestContext context) {
        System.out.println(Colors.GREY_LINE + "\n" +
            " App: \u001B[34m"+current_application.getAppName()+"\u001B[0m\n" +
            " Nº Chats: "+numChats + "\n" +
            " Nº Users per chat: "+usersPerChat + "\n" +
            Colors.GREY_LINE
        );
        currentResult = new Result(
                numChats, 
                usersPerChat, 
                current_application.getAppName(), 
                current_application.getGlobalDefinition(), 
                current_application.getSpecificDefinition(),
                REPEAT_LIMIT
        );
    }
    
    @Test public void attempt0(TestContext context){ test(context, 1); }
    @Test public void attempt1(TestContext context){ test(context, 2); }
    @Test public void attempt2(TestContext context){ test(context, 3); }
    @Test public void attempt3(TestContext context){ test(context, 4); }
    @Test public void attempt4(TestContext context){ test(context, 5); }
    @Test public void attempt5(TestContext context){ test(context, 6); }
    @Test public void attempt6(TestContext context){ test(context, 7); }
    @Test public void attempt7(TestContext context){ test(context, 8); }
    @Test public void attempt8(TestContext context){ test(context, 9); }
    @Test public void attempt9(TestContext context){ test(context, 10); }
    

    @Test
    public void zzTest(TestContext context) {
        JsonObject result = currentResult.toJson();
        System.out.println(Colors.GREY_LINE);
        System.out.println(" Average time: "+total_avg_time/REPEAT_LIMIT);
        System.out.println(" Average cpu use: "+result.getValue("avgCpuUse"));
        System.out.println(" Average memory use: "+result.getValue("avgMemoryUse"));
        System.out.println(Colors.GREY_LINE);
        TestResultsServer.sendResult(result);
        total_avg_time = 0;
        context.assertTrue(true);
    }

    public void test(TestContext context, int attempt){
        Async async = context.async();
        start = 0;
        
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
        
        if(current_application.isAtSameMachine()){
            vertx.setPeriodic(1000, id -> 
                ChatTest.currentResult.addMetric(current_application.getMetrics())
            );
        }

    }


    public void newclient(final String name, String chatName, final long totalMessages, Async async, int attempt) {
        final AtomicInteger numberOfMessages = new AtomicInteger(0);

        vertx.createHttpClient().websocket(current_application.getPort(), current_application.getAddress(), "/chat", websocket -> {

                websocket.handler((Buffer buffer) -> {
                    String respuesta = ChatTestUtils.parseBuffer(buffer);
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
            System.out.println(" Attempt "+attempt+" -> "+Colors.ANSI_GREEN+avg_time+" ms"+ Colors.ANSI_RESET );
            total_avg_time += avg_time;
            async.complete();
        }
    }

}
