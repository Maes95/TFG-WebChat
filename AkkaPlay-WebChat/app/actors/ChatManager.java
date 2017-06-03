package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import messages.GetChat;
import messages.UnsubscribeChatManager;
import play.libs.Akka;

import java.util.HashMap;
import java.util.Map;

public class ChatManager extends UntypedActor{

    private final Map<String,ActorRef> chats;

    public ChatManager() {
        chats = new HashMap<>();
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GetChat) {
            GetChat gcMessage = (GetChat) message;
            //If i don't  have this chat, I create it
            if (!chats.containsKey(gcMessage.getChatname()))            
                chats.put(gcMessage.getChatname(), Akka.system().actorOf(Chat.props(gcMessage.getChatname())));
            gcMessage.setChat(chats.get(gcMessage.getChatname()));
            getSender().tell(message, getSelf());
        } else {
            if (message instanceof UnsubscribeChatManager) {
                chats.remove(((UnsubscribeChatManager) message).getChat());
            } else {
                unhandled(message);
            }
        }
    }
}
