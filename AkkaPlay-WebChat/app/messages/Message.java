package messages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.io.Serializable;

public class Message implements Serializable{
    private String name;
    private String message;
    private String color;

    public Message (String name, String message, String color){
        this.name = name;
        this.color = color;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public ObjectNode getJson () {
        ObjectNode msgdata = Json.newObject();
        msgdata.put("name",name);
        msgdata.put("message",message);
        msgdata.put("color",color);
        return msgdata;
    }
}
