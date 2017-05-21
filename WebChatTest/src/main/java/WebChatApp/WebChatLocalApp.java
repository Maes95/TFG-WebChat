package WebChatApp;

import client.Metrics;
import client.TestMetrics;

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
