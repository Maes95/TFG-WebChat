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
public interface WebChatApp {
    
    public void run();
    
    public Metrics getMetrics();
    
    public void destroy();
    
    public String getAppName();
    
    public String getAddress();
    
    public int getPort();
    
    public boolean isAtSameMachine();

    /**
     * @return the globalDefinition
     */
    public String getGlobalDefinition();

    /**
     * @return the specificDefinition
     */
    public String getSpecificDefinition();
}
