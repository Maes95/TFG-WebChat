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
    private final ArrayList<Metrics> metrics;
    
    public Result(int chatSize, int numUsers, String app, int repeat_limit){
        this.repeat_limit = repeat_limit;
        this.times = new ArrayList<>();
        this.metrics = new ArrayList<>();
        this.chatSize = chatSize;
        this.numUsers = numUsers;
        this.app = app;
    }
    
    public JsonObject toJson(){
        JsonObject response = new JsonObject();
        response.put("chatSize", this.chatSize);
        response.put("numUsers", this.numUsers);
        response.put("app", this.app);
        
        // TIMES
        JsonArray timesList = new JsonArray();
        long avg_time = 0;
        for(long time : this.times){
            avg_time += time;
            timesList.add(time);           
        }
        avg_time = avg_time / repeat_limit;
        response.put("avgTime", avg_time);
        response.put("times", timesList);
        
        // METRICS
        JsonArray cpuUseList = new JsonArray();
        JsonArray memoryUseList = new JsonArray();
        double avg_cpu_use = 0;
        double avg_memory_use = 0;
        for(Metrics metric : this.metrics){
            avg_cpu_use += metric.getCpu();
            avg_memory_use += metric.getMemory();
            cpuUseList.add(metric.getCpu());
            memoryUseList.add(metric.getMemory());          
        }
        avg_cpu_use = avg_cpu_use / this.metrics.size();
        response.put("avgCpuUse", avg_cpu_use);
        response.put("cpuUseList", cpuUseList);
        avg_memory_use = avg_memory_use / this.metrics.size();
        response.put("avgMemoryUse", avg_memory_use);
        response.put("memoryUseList", memoryUseList);
        
        return response;
    }
    
    public void addTime(long time){
        this.times.add(time);
    }    
    
    public void addMetric(Metrics metric){
        this.metrics.add(metric);
    }

}
