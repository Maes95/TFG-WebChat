/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app.WebChatApp;

import com.globex.app.Metrics;
import com.globex.app.TestMetrics;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michel
 */
public class WebChatThrowableApp extends AbstractWebChatApp{
    
    protected static final String PATH = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - "WebChatTest".length());
    
    private final List<String> commands;
    private final String folderName;
    private final int delay;
    
    private Process process;
    private long pid;
    

    public WebChatThrowableApp(String appName, String address, int port, String globalDefinition, String specificDefinition, List<String> commands, String folderName, int delay) {
        super(appName, address, port, globalDefinition, specificDefinition);
        this.commands = commands;
        this.folderName = folderName;
        this.delay = delay;
        
    }

    @Override
    public void run() {
        try {
            process = new ProcessBuilder(commands)
                    .directory(new File(PATH+folderName))
                    .redirectOutput(new File(this.getAppName()+"_log.txt"))
                    .redirectError(new File(this.getAppName()+"_errors.txt"))
                    .start();
        } catch (IOException ex) {
            System.err.println("Can't run application");
        }
        this.pid = TestMetrics.getProcessPID(process);
        System.out.println(" Running "+this.getAppName()+" application");
        System.out.println("  |-> pid: "+pid);
        System.out.println("  |-> command: "+commands);
        System.out.println("  |-> port: "+this.getPort());
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger(WebChatThrowableApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Metrics getMetrics() {
        return TestMetrics.getMetricsTop(pid);
    }

    @Override
    public void destroy() {
        System.out.println(" Stopping "+this.getAppName()+" application");
        process.destroyForcibly();
    }

    @Override
    public boolean isAtSameMachine() {
        return true;
    }
    
}
