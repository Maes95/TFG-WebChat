package actors;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import messages.*;
import play.libs.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class User extends UntypedActor {

    private final ActorRef out;
    private ActorRef chatManager;
    private ActorRef chat;
    private ObjectMapper mapper = new ObjectMapper();
    private String username;
    private String color;
    private List<Message> unsendMessages;

    public static Props props(ActorRef out) {
        return Props.create(User.class, out);
    }

    public static Props props(ActorRef out, ActorRef chatManager) {
        return Props.create(User.class, out, chatManager);
    }

    public User(ActorRef out, ActorRef chatManager) {
        this.out = out;
        this.chatManager = chatManager;
        this.color = getRandomColor();
        this.unsendMessages = new ArrayList<Message>();
    }

    public void onReceive(Object message) throws Exception {
        //Message from the client
        if (message instanceof String) {
            JsonNode json= null;
            try {
                json= mapper.readTree(message.toString());  //message to Json
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Initial message. Send the chat name to ChatManager
            if (!json.has("message")){
                username = json.get("name").asText();
                GetChat getChat = new GetChat(json.get("chat").asText());
                chatManager.tell(getChat, getSelf());
            }
            //Normal message. Send message to chat
            else{
                Message msg = new Message(json.get("name").asText(),json.get("message").asText(),color);
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

                    for (Message msg: unsendMessages){
                        chat.tell(msg, getSelf());
                    }
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
    public void postStop() throws Exception {
        if (chat!=null){ //If I was connected to any chat, I unsubscribe
            UnsubscribeChat unsubscribeChat = new UnsubscribeChat(username);
            chat.tell(unsubscribeChat,getSelf());
        }
    }

    public String getRandomColor() {
        String[] letters = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "E", "F"};
        String color = "";
        for (int i = 0; i < 6; i++) {
            color = color.concat(letters[(int) (Math.random() * 15)]);
        }
        return color;
    }
}
