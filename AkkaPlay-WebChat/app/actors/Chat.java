package actors;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import messages.*;
import play.libs.Akka;

import java.util.HashMap;
import java.util.Map;

public class Chat extends UntypedActor{

    private final Map<String, ActorRef> users;
    private final ActorRef chatManager;
    private final String chatName;

    public static Props props(String chatName) {
        return Props.create(Chat.class, chatName);
    }

    public Chat(String chatName) {
        this.chatName = chatName;
        this.chatManager = Akka.system().actorFor("akka://application/user/ChatManager");
        this.users = new HashMap<>();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        // Normal message from user
        if (message instanceof Message){
            users.entrySet().forEach((entry) -> {
                    entry.getValue().tell(message, getSelf());
            });
        }
        //Suscribe message
        else{
            if (message instanceof SubscribeChat){
                if (users.containsKey(((SubscribeChat) message).getUser())){ 
                    //If I already have this user
                    getSender().tell(new DuplicatedUser(((SubscribeChat) message).getUser()), getSelf());
                }else{ 
                    //If is a new user, I subscribe it
                    users.put(((SubscribeChat) message).getUser(), getSender());
                }
            }else{
                if (message instanceof UnsubscribeChat) {
                    users.remove(((UnsubscribeChat) message).getUser());
                    if (users.isEmpty()) { 
                        //If there aren't clients in this chat, I remove this chat
                        chatManager.tell(new UnsubscribeChatManager(chatName), getSelf());
                        self().tell(PoisonPill.getInstance(), self());
                    }
                } else {
                    unhandled(message);
                }
            }
        }
    }
}