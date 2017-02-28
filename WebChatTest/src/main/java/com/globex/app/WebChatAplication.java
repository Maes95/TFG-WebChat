package com.globex.app;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class WebChatAplication {
    
    private static final String PATH = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - 11);
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 9000;
    private static final int DEFAULT_DELAY = 10000;
    
    private String folderName;
    private String appName;
    private String address;
    private int port;
    private List<String> commands;
    private int delay;
    private boolean remote;
    
    private Process process;
    private long pid;
    
    public WebChatAplication(JSONObject config){
        try{
            this.appName = config.getString("name");
            this.folderName = config.isNull("folderName") ? appName+"-WebChat" : config.getString("folderName");
            this.port = config.isNull("port") ? DEFAULT_PORT : config.getInt("port");
            if(!config.isNull("remote") && config.getBoolean("remote")){
                // REMOTE APPLICATION
                this.remote = true;
                this.address = config.isNull("address") ? LOCAL_ADDRESS : config.getString("address");
                this.delay = 0;
                this.pid = config.isNull("pid") ? -1 : config.getInt("pid");
            }else{
                // LOCAL APPLICATION
                this.remote = false;
                this.address = LOCAL_ADDRESS;
                this.delay = config.isNull("delay") ? DEFAULT_DELAY : config.getInt("delay");
                this.commands = Arrays.asList(config.getString("commands").split("\\s+"));
            }
        }catch(JSONException ex){
            System.err.println("Error at configuration, check WebChatTest/src/main/resources/config.json");
            ex.printStackTrace();
        }
    }
    
    public void run(){
        // IS A LOCAL APPLICATION, NEED TO RUN IT
        if(!this.remote){
            try {
                process = new ProcessBuilder(commands)
                .directory(new File(PATH+folderName))
                .redirectOutput(new File(appName+"_log.txt"))
                .redirectError(new File(appName+"_errors.txt"))
                .start();
                this.pid = TestMetrics.getProcessPID(process);
                System.out.println("Running "+appName+" application");
                System.out.println(" |-> pid: "+pid);
                System.out.println(" |-> command: "+commands);
                System.out.println(" |-> port: "+port);
                Thread.sleep(delay);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }else{
            System.out.println("Connecting to remote "+appName+" application");
        }
    }
    
    public Metrics getMetrics(){
        return TestMetrics.getMetricsTop(pid);
    }
    
    public void destroy(){
        if(!this.remote){
            System.out.println("Stopping "+appName+" application");
            process.destroy();
        }else{
            System.out.println("Desconecting from remote "+appName+" application");
        }
        System.out.println("-------------------------------------------------------");
    }
    
    public String getAppName(){
        return this.appName;
    }
    
    public String getAddress(){
        return this.address;
    }
    
    public int getPort(){
        return this.port;
    }
    
    public boolean isRemote(){
        return this.remote;
    }
    
    public boolean isAtSameMachine(){
        return !this.remote || (this.pid != -1);
    }
}
