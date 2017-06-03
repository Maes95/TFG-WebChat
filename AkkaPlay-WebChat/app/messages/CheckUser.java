package messages;

import java.io.Serializable;

public class CheckUser implements Serializable{

    private final String user;

    public CheckUser (String user){
        this.user = user;
    }

    public String getUser(){
        return user;
    }

}
