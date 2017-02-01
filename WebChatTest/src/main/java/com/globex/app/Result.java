package com.globex.app;


import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author michel
 */
public class Result {
    
    private final int chatSize;
    private final int numUsers;
    private final int repeat_limit;
    private final String app;
    private final ArrayList<Long> times;
    
    public Result(int chatSize, int numUsers, String app, int repeat_limit){
        this.repeat_limit = repeat_limit;
        this.times = new ArrayList<>();
        this.chatSize = chatSize;
        this.numUsers = numUsers;
        this.app = app;
    }
    
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
        avg_time = avg_time / repeat_limit;
        response.put("avgTime", avg_time);
        response.put("app", this.app);
        response.put("times", timesList);
        return response;
    }
    
    public void addTime(long time){
        this.times.add(time);
    }    

}
