import actors.ChatManager;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.GlobalSettings;
import play.libs.Akka;

public class Global extends GlobalSettings {

    private ActorRef chatManager;

    @Override
    public void onStart(play.Application application) {
        super.onStart(application);
        chatManager = Akka.system().actorOf(Props.create(ChatManager.class), "ChatManager");
    }

    public ActorRef getChatManager() {
        return chatManager;
    }
}