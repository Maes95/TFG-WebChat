package ChatTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Colors;
import utils.JSONFile;

/**
 *
 * @author michel
 */
public class ChatTestUtils {
    
    private static final String PATH_TO_CONFIG = System.getProperty("user.dir")+"/src/main/resources/config.json";
    
    public static Collection<Object[]> getConfig(){
        JSONObject properties = JSONFile.parse(PATH_TO_CONFIG);

        List<Object[]> params = new ArrayList<>();
        
        // Names of aplications which participate in the test
        ((JSONArray) properties.get("apps")).forEach((_app) -> {
            // Number of chat romms
            ((JSONArray) properties.get("chats")).forEach((chat) -> {
                JSONObject _chat = (JSONObject) chat;
                int _numChats = _chat.getInt("numChats");
                // Number of users in chat
                ((JSONArray) _chat.get("users")).forEach((_numUsers) -> {
                    Object[] o = { _numUsers, _numChats, _app };
                    params.add(o);
                });
            });
        });
        
        return params;
    }
    
    public static void printHeader(){
        System.out.println(Colors.LOW_INTENSITY+" _    _      _     _____ _           _ _____         _   \n" +
                                                "| |  | |    | |   /  __ \\ |         | |_   _|       | |  \n" +
                                                "| |  | | ___| |__ | /  \\/ |__   __ _| |_| | ___  ___| |_ \n" +
                                                "| |/\\| |/ _ \\ '_ \\| |   | '_ \\ / _` | __| |/ _ \\/ __| __|\n" +
                                                "\\  /\\  /  __/ |_) | \\__/\\ | | | (_| | |_| |  __/\\__ \\ |_ \n" +
                                                " \\/  \\/ \\___|_.__/ \\____/_| |_|\\__,_|\\__\\_/\\___||___/\\__|\n"+Colors.ANSI_RESET);
    }
    
    public static String parseBuffer(Buffer buff){
        JsonNode message = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            message = mapper.readTree(buff.getBytes());  //message to Json
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.get("message").asText();
    }
    
}
