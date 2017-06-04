package messages;

import java.io.Serializable;

public class SubscribeChat implements Serializable{

    private final String user;

    public SubscribeChat (String user){
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
