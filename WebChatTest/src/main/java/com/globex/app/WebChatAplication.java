package com.globex.app;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class WebChatAplication {
    
    private static final String PATH = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - 11);
    private static final String[] DEFAULT_COMMANDS = {"./run.sh"};
    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 9000;
    private static final int DELAY = 10000;
    
    private final String folderName;
    private final String appName;
    private final String address;
    private final int port;
    private final List<String> commands;
    
    private Process process;
    private long pid;
    
    public WebChatAplication(String appName, String address, int port){
        this.appName = appName;
        this.folderName = appName+"-WebChat";
        this.address = address;
        this.port = port;
        this.commands = Arrays.asList(DEFAULT_COMMANDS);
    }
    
    public WebChatAplication(JSONObject config){
        this.appName = config.getString("name");
        this.folderName = config.isNull("folderName") ? appName+"-WebChat" : config.getString("folderName");
        this.address = config.isNull("address") ? DEFAULT_ADDRESS : config.getString("address");
        this.port = config.isNull("port") ? DEFAULT_PORT : config.getInt("port");
        if(config.isNull("commands")){
            this.commands = Arrays.asList(DEFAULT_COMMANDS);;
        }else{
            this.commands = Arrays.asList(config.getString("commands").split("\\s+"));
        }
    }
    
    public void run(){
        // IS A LOCAL APPLICATION, NEED TO RUN IT
        if(this.address.equals(DEFAULT_ADDRESS)){
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
                Thread.sleep(DELAY);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public Metrics getMetrics(){
        return TestMetrics.getMetricsTop(pid);
    }
    
    public void destroy(){
        System.out.println("Stopping "+appName+" application");
        System.out.println("-------------------------------------------------------");
        process.destroy();
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
}
