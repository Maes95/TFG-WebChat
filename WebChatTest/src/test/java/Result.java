
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author michel
 */
public class Result {
    
    private int chatSize;
    private int numUsers;
    private String app;
    private ArrayList<Long> times;
    
    public Result(){}
    
    public JsonObject toJson(){
        JsonObject response = new JsonObject();
        response.put("chatSize", this.chatSize);
        response.put("numUsers", this.numUsers);
        JsonArray timesList = new JsonArray();
        long avg_time = 0;
        for(long time : this.times){
            avg_time += time;
            timesList.add(time);           
        }
        avg_time = avg_time / ChatTest.REPEAT_LIMIT;
        response.put("avgTime", avg_time);
        response.put("app", this.app);
        response.put("times", timesList);
        return response;
    }
    
    public void addTime(long time){
        this.times.add(time);
    }    

    public void setUp(int chatSize, int numUsers, String app) {        
        this.times = new ArrayList<>();
        this.chatSize = chatSize;
        this.numUsers = numUsers;
        this.app = app;
    }

}
