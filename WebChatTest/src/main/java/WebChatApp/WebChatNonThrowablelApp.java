package WebChatApp;

import client.Metrics;
import utils.Colors;

/**
 *
 * @author michel
 */
public abstract class WebChatNonThrowablelApp extends AbstractWebChatApp{

    public WebChatNonThrowablelApp(String appName, String address, int port, String globalDefinition, String specificDefinition) {
        super(appName, address, port, globalDefinition, specificDefinition);
    }

    @Override
    public void run() {
        System.out.println(" Connecting to remote "+this.getAppName()+" application");
    }

    @Override
    public abstract Metrics getMetrics();

    @Override
    public void stop() {
        System.out.println(" Disconnecting from remote "+this.getAppName()+" application");
        System.out.println(Colors.GREY_LINE);
    }

    @Override
    public abstract boolean isAtSameMachine();
    
}
