# TFG-WebChat

This repository is about a comparison between 3 different reactive technologies: Akka, Vertx and Node.js

The proyects which participates in this comparison are:
- WebChatAkkaPlay
- WebChatVertxWebsockets
- WebChatNodeWebsockets

## How test web servers

Clone the proyect
```sh
$ git clone https://github.com/Maes95/TFG-WebChat.git
```
Select a web server, set up and run it (Select a project to see the specific documentation)

When server is running, run client test (need Maven 3.0.5):

```sh
$ cd WebChatTest
$ mvn test
```
Then, you can see at console something like this:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running ChatTest
-------------------------------------------------------
Nº Chats: 4
Nº Users per chat: 20
Attempt 1: 26509
Attempt 2: 25940
Attempt 3: 26293
Attempt 4: 25872
Attempt 5: 26365
Attempt 6: 26055
Attempt 7: 26069
Attempt 8: 26165
Attempt 9: 26158
Attempt 10: 26916
Average time: 26234
```
Number of users and chats can be set up at test parameters:

```java
@Parameters
public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
             // N users / 1 chat room
             { 10, 1 }, { 20, 1 }, { 30, 1 },
             { 40, 1 }, { 50, 1 }, { 60, 1 }, { 70, 1 },
             // N users / 2 chat rooms
             { 20, 2 }, { 25, 2 }, { 30, 2 }, { 35, 2 },
             // N users / 4 chat rooms
             { 10, 4 }, { 12, 4 }, { 15, 4 }, { 17, 4 },
    });
}
```




