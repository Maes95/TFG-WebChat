package messages;

import java.io.Serializable;

public class UnsubscribeChat implements Serializable{

    private String user;

    public UnsubscribeChat (String user){
        this.user=user;
    }

    public String getUser() {
        return user;
    }
}
