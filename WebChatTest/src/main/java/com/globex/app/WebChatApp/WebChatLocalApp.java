/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app.WebChatApp;

import com.globex.app.Metrics;
import com.globex.app.TestMetrics;

/**
 *
 * @author michel
 */
public class WebChatLocalApp extends WebChatNonThrowablelApp{

    private final long pid;

    public WebChatLocalApp(String appName, String address, int port, String globalDefinition, String specificDefinition, long pid) {
        super(appName, address, port, globalDefinition, specificDefinition);
        this.pid = pid;
    }

    @Override
    public Metrics getMetrics() {
        return TestMetrics.getMetricsTop(pid);
    }

    @Override
    public boolean isAtSameMachine() {
        return true;
    }
    
}
