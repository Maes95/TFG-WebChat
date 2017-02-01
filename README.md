# TFG-WebChat

This repository is about a comparison between 7 different technologies.

The proyects which participates in this comparison are:
- AkkaPlay
- NodeJSCluster
- NodeJS
- SpringBoot-Jetty
- SpringBoot-Tomcat
- SpringBoot-Undertow
- Vertx

## How test web servers

Clone the proyect
```sh
$ git clone https://github.com/Maes95/TFG-WebChat.git
```
Set up all servers, including WebChatTest (Select a project to see the specific documentation)

Run client test (need Maven 3.0.5):

```sh
$ cd WebChatTest
$ mvn test
```
Then, you can see results at localhost:8080 (Opens automatically by aplication).

![alt text](https://s29.postimg.org/q8dbxqbk7/N_usuarios_en_1_sala_s_de_chat.png)

Also, you can see results at console like this:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running ChatTest
Starting NodeJS application
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





