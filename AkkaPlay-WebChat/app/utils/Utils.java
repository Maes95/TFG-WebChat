package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author michel
 */
public class Utils {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static JsonNode getJson(String message){
        JsonNode json= null;
        try {
            json= mapper.readTree(message);  //message to Json
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
