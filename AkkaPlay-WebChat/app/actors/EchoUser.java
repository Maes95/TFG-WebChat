package actors;

import akka.actor.*;

public class EchoUser extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(EchoUser.class, out);
    }

    private final ActorRef out;

    public EchoUser(ActorRef out) {
        this.out = out;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            out.tell("I received your message: " + message, self());
        }
    }
}
