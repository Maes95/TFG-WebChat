package WebChatApp;

import org.json.JSONObject;

/**
 *
 * @author michel
 */
public interface WebChatAppFactoryMethod {
    
    public WebChatApp createWebChatApp(JSONObject config);
    
}
