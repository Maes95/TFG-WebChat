# WebChatAkkaPlay

This is a distributed WebChat with playFramework and akka.

## SetUp

* Because SBT can't resolve some dependences, you need to start a server with activator to resolve this dependences before importing the project in your ide like a SBT project.

```
git clone https://github.com/Maes95/TFG-WebChat.git
cd TFG-WebChat/WebChatAkkaPlay
./activator
run 9000
```

Open http://localhost:9000/ in your browser to see client (open this page in other browser to check chat interaction)
