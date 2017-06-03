package actors;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.*;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;
import play.libs.Akka;


public class User extends UntypedActor {

    private final ActorRef out;
    private final ActorRef chatManager;
    private final List<Message> unsendMessages;
    private ActorRef chat;
    private String username;

    public static Props props(ActorRef out) {
        return Props.create(User.class, out, Akka.system().actorFor("akka://application/user/ChatManager"));
    }

    public User(ActorRef out, ActorRef chatManager) {
        this.out = out;
        this.chatManager = chatManager;
        this.unsendMessages = new ArrayList<>();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        //Message from the client
        if (message instanceof String) {
            JsonNode json= utils.Utils.getJson((String) message);
            //Initial message. Send the chat name to ChatManager
            if (!json.has("message")){
                username = json.get("name").asText();
                GetChat getChat = new GetChat(json.get("chat").asText());
                chatManager.tell(getChat, getSelf());
            }
            // Normal message, send to the chat
            else{
                Message msg = new Message(json.get("name").asText(),json.get("message").asText());
                //To avoid the first messages to be sent before the chat actorRef is received
                if (chat!= null){
                    chat.tell(msg, getSelf());
                }else{
                    unsendMessages.add(msg);
                }
            }
        }else{
            // Message from chat to client
            if (message instanceof Message){
                out.tell(((Message) message).getJson().toString(), self());
            }else{
                // Returned message sent by ChatManager. Sends suscribe message
                if (message instanceof GetChat) {
                    SubscribeChat subscribeChat = new SubscribeChat(username);
                    chat = ((GetChat)message).getChat();
                    chat.tell(subscribeChat,getSelf());

                    unsendMessages.forEach((msg) -> {
                        chat.tell(msg, getSelf());
                    });
                    unsendMessages.clear();
                }else{
                    if (message instanceof DuplicatedUser){
                        ObjectNode msg = Json.newObject();
                        msg.put("type", "system");
                        msg.put("message", "This user already exists");
                        chat = null; //To avoid the dead letters when this actor do the postStop and the Chat reply
                        out.tell(msg.toString(),getSelf());
                        self().tell(PoisonPill.getInstance(), self());
                    }
                }
            }
        }
    }

    // When the websocket are closed
    @Override
    public void postStop() throws Exception {
        if (chat!=null){ //If I was connected to any chat, I unsubscribe
            UnsubscribeChat unsubscribeChat = new UnsubscribeChat(username);
            chat.tell(unsubscribeChat,getSelf());
        }
    }
    
}
