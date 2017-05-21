package WebChatApp;

import client.Metrics;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class WebChatAppFactory implements WebChatAppFactoryMethod{
    
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 9000;
    private static final int DEFAULT_DELAY = 10000;

    @Override
    public WebChatApp createWebChatApp(JSONObject config) {
        if (config == null) throw new NullPointerException();
        WebChatApp app = null;
        try{
            String name = config.getString("name");
            String globalDefinition = config.isNull("globalDefinition") ? "No definition provided" : config.getString("globalDefinition");
            String specificDefinition = config.isNull("specificDefinition") ? "" : config.getString("specificDefinition");
            
            int port = config.isNull("port") ? DEFAULT_PORT : config.getInt("port");
            
            if(!config.isNull("remote") && config.getBoolean("remote")){
                app = new WebChatRemote(
                    name, 
                    LOCAL_ADDRESS, 
                    port, 
                    globalDefinition, 
                    specificDefinition, 
                        () -> { return null; });
                throw new Error("Remote applications not implemented yet");
            }else if(config.isNull("commands")){
                // LOCAL APPLICATION
                
                app = new WebChatLocalApp(
                    name, 
                    LOCAL_ADDRESS, 
                    port, 
                    globalDefinition, 
                    specificDefinition,
                    config.getInt("pid")
                );
            }else{
                // THROWABLE APPLICATION
                String folderName = config.isNull("folderName") ? name+"-WebChat" : config.getString("folderName");
                app = new WebChatThrowableApp(
                    name, 
                    LOCAL_ADDRESS, 
                    port, 
                    globalDefinition, 
                    specificDefinition,
                    Arrays.asList(config.getString("commands").split("\\s+")),
                    folderName,
                    config.isNull("delay") ? DEFAULT_DELAY : config.getInt("delay")
                );
            }
        }catch(JSONException ex){
            System.err.println("Error at configuration, check WebChatTest/src/main/resources/config.json");
            ex.printStackTrace();
        }
        
        return app;
    }
    
}
