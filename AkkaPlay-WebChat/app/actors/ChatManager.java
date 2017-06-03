package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.*;
import akka.cluster.pubsub.DistributedPubSub;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import messages.GetChat;
import messages.GetIP;
import messages.UnsubscribeChatManager;
import play.Play;
import play.libs.Akka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager extends UntypedActor{

    private Map<String,ActorRef> chats;
    private List <String> ips;
    Cluster cluster = Cluster.get(getContext().system());
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ActorRef router = getContext().actorOf(FromConfig.getInstance().props(), "router");
    ActorRef mediator;


    public ChatManager() {
        ips = new ArrayList<String>();
        ips.add("\""+Play.application().configuration().getString("akka.remote.netty.tcp.hostname").toString()+"\"");
        chats = new HashMap<String,ActorRef>();
        mediator = DistributedPubSub.get(getContext().system()).mediator();
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        //#subscribe
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            //Runtime runtime = Runtime.getRuntime();
            //System.out.print("Heap: " + (runtime.maxMemory()-(runtime.totalMemory()-runtime.freeMemory())));
            //System.out.println(" / " + runtime.maxMemory());
            router.tell(new GetIP(), getSender());
        } else {
            if (message instanceof GetIP) {
                getSender().tell(ips.toString(), getSelf());
            } else {
                if (message instanceof GetChat) {
                    if (!chats.containsKey(((GetChat) message).getChatname())) { //If i don't  have this chat, I create it
                        chats.put(((GetChat) message).getChatname(), Akka.system().actorOf(Chat.props(((GetChat) message).getChatname(), mediator)));
                        ((GetChat) message).setChat(chats.get(((GetChat) message).getChatname()));
                        getSender().tell(message, getSelf());
                    } else { //If I already have this chat, only I send it back the ActorRef of the chat (inside the same message I've received)
                        ((GetChat) message).setChat(chats.get(((GetChat) message).getChatname()));
                        getSender().tell(message, getSelf());
                    }
                } else {
                    if (message instanceof UnsubscribeChatManager) {
                        chats.remove(((UnsubscribeChatManager) message).getChat());
                    } else {
                        if (message instanceof MemberUp) {
                            MemberUp mUp = (MemberUp) message;
                            log.info("Member is Up: {}", mUp.member());
                            if (!ips.contains("\""+mUp.member().address().host().get().toString()+"\"")) {
                                ips.add("\"" + mUp.member().address().host().get().toString() + "\"");
                            }
                            System.out.println(ips.toString());

                        } else if (message instanceof UnreachableMember) {
                            UnreachableMember mUnreachable = (UnreachableMember) message;
                            log.info("Member detected as unreachable: {}", mUnreachable.member());

                        } else if (message instanceof MemberRemoved) {
                            MemberRemoved mRemoved = (MemberRemoved) message;
                            log.info("Member is Removed: {}", mRemoved.member());
                            ips.remove("\""+mRemoved.member().address().host().get().toString()+"\"");

                        } else if (message instanceof MemberEvent) {
                            // ignore

                        } else if (message instanceof CurrentClusterState) {
                            CurrentClusterState state = (CurrentClusterState) message;
                        } else {
                            unhandled(message);
                        }
                    }
                }
            }
        }
    }
}
