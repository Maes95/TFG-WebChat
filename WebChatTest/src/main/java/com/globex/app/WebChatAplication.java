package com.globex.app;


import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
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
    
    private final String folderName;
    private final String appName;
    private final String address;
    private final int port;
    private final String[] commands;
    
    private Process process;
    
    public WebChatAplication(String appName, String address, int port){
        this.appName = appName;
        this.folderName = appName+"-WebChat";
        this.address = address;
        this.port = port;
        this.commands = DEFAULT_COMMANDS;
        System.out.println("Starting "+appName+" application");
    }
    
    public WebChatAplication(JSONObject config){
        this.appName = config.getString("name");
        this.folderName = config.isNull("folderName") ? appName+"-WebChat" : config.getString("folderName");
        this.address = config.isNull("address") ? DEFAULT_ADDRESS : config.getString("address");
        this.port = config.isNull("port") ? DEFAULT_PORT : config.getInt("port");
        this.commands = config.isNull("port") ? DEFAULT_COMMANDS : config.getJSONArray("commands").join(",").split(",");
        System.out.println("Starting "+appName+" application");
    }
    
    public void run(){
        // IS A LOCAL APPLICATION, NEED TO RUN IT
        if(this.address.equals(DEFAULT_ADDRESS)){
            try {
                process = new ProcessBuilder(commands).directory(new File(PATH+folderName)).start();
                Thread.sleep(5000);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void showMetrics(){
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpu_used = Math.round(operatingSystemMXBean.getProcessCpuLoad() * 100.0) / 100.0;
        Runtime r = Runtime.getRuntime();
        double memory_used = Math.round(((r.totalMemory() - r.freeMemory())/ r.maxMemory())* 100.0) / 100.0;
        
        System.out.println(" -> CPU: "+cpu_used+" %");
        System.out.println(" -> MEMORY: "+memory_used+" %");
    }
    
    public void destroy(){
        System.out.println("Stopping "+appName+" application");
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
