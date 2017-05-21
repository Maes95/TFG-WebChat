package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class JSONFile {
    
    public static JSONObject parse(String filePath){
        JSONObject json = new JSONObject();
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            json =  new JSONObject(result);
        } catch(IOException e) {
            System.err.println("Can't read json file");
            e.printStackTrace();
        }
        return json;
    }
}
