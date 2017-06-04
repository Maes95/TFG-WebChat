package messages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.io.Serializable;

public class Message implements Serializable{
    private final String name;
    private final String message;

    public Message (String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public ObjectNode getJson () {
        ObjectNode msgdata = Json.newObject();
        msgdata.put("name",name);
        msgdata.put("message",message);
        return msgdata;
    }
}
