# WebChatAkkaPlay

This is a distributed webChat with playFramework and akka.

IMPORTANT:
Because SBT can't resolve some dependences, you need to start a server with activator to resolve this dependences before importing the project in your ide like a SBT project.


To start a server, you should do inside the project folder:

    you@yourPC:~$PathToTheProject/WebChatPlayAkka$ ./activator

    [play-java] $ run 9000

To start a second server, you must change the port in application.conf (ln 54):

        remote {
                log-remote-lifecycle-events = off
                netty.tcp {
                  hostname = "127.0.0.1"
                  port = 8001
                }
              }

And now, you can start the new server in port 9001:

    you@yourPC:~$PathToTheProject/WebChatPlayAkka$ ./activator

    [play-java] $ run 9001
