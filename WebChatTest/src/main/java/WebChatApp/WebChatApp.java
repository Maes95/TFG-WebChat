package WebChatApp;

import client.Metrics;

/**
 *
 * @author michel
 */
public interface WebChatApp {
    
    /**
     * Starts de application
     */ 
    public void run();
    
    /**
     * Disconect and/or destroy de application
     */
    public void stop();
    
    /**
     * @return current metrics from de application
     */
    public Metrics getMetrics();
    
    /**
     * @return name from application 
     */
    public String getAppName();
    
    /**
     * @return adress from application 
     */
    public String getAddress();
    
    /**
     * @return port number where application listen
     */
    public int getPort();
    
    /**
     * @return if application runs at same machine as test process
     */
    public boolean isAtSameMachine();
    
    /**
     * @return global definition off application
     */
    public String getGlobalDefinition();
    
    /**
     * @return specific definition off application
     */
    public String getSpecificDefinition();
}
