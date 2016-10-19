package messages;

import akka.actor.ActorRef;

import java.io.Serializable;

public class GetChat implements Serializable{
    private String chatname;
    private ActorRef chat;

    public GetChat (String chatname){
        this.chatname = chatname;
        this.chat = null;
    }

    public ActorRef getChat() {
        return chat;
    }

    public String getChatname() {
        return chatname;
    }

    public void setChat(ActorRef chat) {
        this.chat = chat;
    }
}

