/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app.WebChatApp;

import com.globex.app.Metrics;

/**
 *
 * @author michel
 */
public abstract class AbstractWebChatApp implements WebChatApp {
    
    private final String appName;
    private final String address;
    private final int port;
    private final String globalDefinition;
    private final String specificDefinition;
    
    protected AbstractWebChatApp(String appName, String address, int port, String globalDefinition, String specificDefinition){
        this.appName = appName;
        this.address = address;
        this.port = port;
        this.globalDefinition = globalDefinition;
        this.specificDefinition = specificDefinition;
    }

    @Override
    public abstract void run();

    @Override
    public abstract Metrics getMetrics();

    @Override
    public abstract void destroy();

    @Override
    public abstract boolean isAtSameMachine();

    @Override
    public String getAppName() {
       return this.appName;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getGlobalDefinition() {
        return this.globalDefinition;
    }

    @Override
    public String getSpecificDefinition() {
        return this.specificDefinition;
    }
    
}
