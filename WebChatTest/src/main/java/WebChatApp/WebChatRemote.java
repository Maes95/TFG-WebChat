package WebChatApp;

import client.GetMetricsFunction;
import client.Metrics;

/**
 *
 * @author michel
 */
public class WebChatRemote extends WebChatNonThrowablelApp{
    
    private final GetMetricsFunction metricFunction;
    
    
    /**
     * Create a new instance of WebChatRemote
     * 
     * @param appName
     * @param address
     * @param port
     * @param globalDefinition
     * @param specificDefinition
     */
    public WebChatRemote(String appName, String address, int port, String globalDefinition, String specificDefinition, GetMetricsFunction function) {
        super(appName, address, port, globalDefinition, specificDefinition);
        this.metricFunction = function;
    }
    
    /**
     * Create a new instance of WebChatRemote with a default metric function
     * that always return a empty Metrics object
     * 
     * @param appName
     * @param address
     * @param port
     * @param globalDefinition
     * @param specificDefinition
     */
    public WebChatRemote(String appName, String address, int port, String globalDefinition, String specificDefinition) {
        super(appName, address, port, globalDefinition, specificDefinition);
        this.metricFunction = () -> new Metrics();
    }
    
    @Override
    public Metrics getMetrics(){
        return metricFunction.getMetrics();
    };

    @Override
    public boolean isAtSameMachine() {
        return false;
    }
    
}
