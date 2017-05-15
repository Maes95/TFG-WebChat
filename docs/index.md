## Introduction

Nowadays, the need of highly scalable distributed systems it's nothing new. One way to approach this need may be through the use of reactive technologies, that following the [Reactive Manifesto](http://www.reactivemanifesto.org/), count among its characteristics:

- **Responsive:** Responsive systems focus on providing rapid and consistent response times, respond in a timely manner if at all possible. 

-  **Resilient:** The system stays responsive in the face of failure. Resilience is achieved by replication, containment, isolation and delegation. The systems must be able to recover without compromising the integrity of the system.

- **Elastic:** The system stays responsive under varying workload. This implies designs that have no contention points or central bottlenecks. 

- **Message Driven:** Reactive Systems rely on asynchronous message-passing to establish a boundary between components that ensures loose coupling, isolation and location transparency.  Communication is non-blocking.

![Reactive Manifesto](https://lh3.googleusercontent.com/-eu3WHr_0sXI/WRdsq1jvL7I/AAAAAAAAA5I/NO1tCOSjzfcU9u_ZX3hysOFApF8C9n0LwCLcB/s0/foto3.jpg "foto3.jpg")

There are many technologies applicable to this problem, the ones chosen for this comparison are the following:

<img class="logo" src="http://akka.io/resources/images/akka_full_color.svg" width="200">

**[Akka (v2.4)](http://http://akka.io/)**: Scala's native library based on the actors model, although for this project we have used its version in Java. In order to launch the application on a server we will use PlayFramework, which includes the Akka library as well as additional libraries for the connection via websocket.

> Applications in the comparison:
>
>- **Akka + PlayFramework**

<img  class="logo" src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Vert.x_Logo.svg/2000px-Vert.x_Logo.svg.png" width="200">

**[Vertx (v3.3.3)](http://vertx.io/)**: Library available in different programming languages ​​to create reactive applications, we will also use its version in Java. This library provides a server to launch the application and websockets. One of its main (and questioned, as we will see later) resources is its EventBus, which is the one that gives it its reactive character. We will compare how it behaves using it or not:

> Applications in the comparison:
>
> - **Vertx with EventBus**
> - **Vertx without EventBus**

<img  class="logo" src="https://nodejs.org/static/images/logos/nodejs-new-pantone-black.png" width="200">

**[Node.js(v5.6.0)](https://nodejs.org)**: Run-time environment for Javascript, based on an event-driven architecture. Node.js runs by default in single thread, although it has libraries to take advantage of all the processors of the machine that uses it. We will contemplate both implementations making use of the Express library to launch the server and ws to handle the connection using websocket.
> Applications in the comparison:
> 
> - **Node.js + Express**
> - **Node.js + Express + Cluster**

<img  class="logo spring-logo" src="http://rubenjgarcia.es/wp-content/uploads/2016/09/springboot.png" width="200">

**[SpringBoot(v1.4.3)](https://projects.spring.io/spring-boot/)**: 
Technology belonging to the Spring ecosystem (Java framework for application development). Although the technology is not reactive, we include it to test and verify the effectiveness of other technologies. You can use the different servers to launch the application, in this comparison we will use:

> Applications in the comparison:
> 
> -  **SpringBoot + Tomcat**
> -  **SpringBoot + Jetty**

The goal of this comparative will be to verify how these technologies will behave as the workload increases, specifically we will pay attention to the times and the use of resources of the machine (how they take advantage of CPU cores and memory usage).

For this purpose, **a distributed chat of identical functioning and a common client for each technology** has been developed for maximum homogeneity in the tests.

The functionating of this chat is simple, the client connects with a user name (which proves to be unique) and the name of a chat room to which it is attached. In this way, the user who connects to the chat room can send and receive messages from other users connected to the same chat.

## Implementation

The implementations of Akka and Vert.x belong to [Javier Mateos](https://github.com/meji92), although Vert.x applications were revised and updated from version 2.1.5 to 3.3.3 of the same library. The source code of the applications used can be found in the following repository, which corresponds to the second version of the project (v2.0):

https://github.com/Maes95/TFG-WebChat/tree/v2.0

Although many technologies rely on external libraries to manage communications with WebSocket (such as Sockjs or Socket.io), the basic implementation provided by each technology has been used.

The client is developed in Java and uses together the JUnit testing libraries and the native Vert.x testing libraries. This implementation can be found in:

https://github.com/Maes95/TFG-WebChat/tree/v2.0/WebChatTest

## Comparative

The tests will be performed on a Linux Mint 17.3 machine with 8 CPUs cores. These tests will consist of launching X clients that will send 500 messages periodically to the rest and store the ones that it receives in an array (to check that all arrive).

To measure the time it takes for messages to be received, the client sending, attached to the message body, the current time and an identifier. When the message arrives, it calculates the time it has taken (current time less the time that it brings in the body) and this is added to a variable, that when arriving all the messages is divided between the total number of messages, obtaining the average time (in milliseconds) that a message takes to transmit.

For the experiment, tests have been performed for different number of chats in different applications. For these tests the client has been tested with different numbers of users (up to 10 iterations have been performed for each number of users to obtain the maximum homogeneity in the results).

Below are the results based on 3 metrics: Response time, CPU usage and consumed memory

### Response time

#### Application with N users in 1 chat room - Time

<div width="400" height="400">
	<canvas id="compare-time-1" ></canvas>
</div>
<script>
	createChart("compare-time-1", 'Time in milliseconds', "avgTime", 1);
</script>

#### Application with N users in 2 chat rooms - Time

<div width="400" height="400">
	<canvas id="compare-time-2" ></canvas>
</div>
<script>
	createChart("compare-time-2", 'Time in milliseconds', "avgTime", 2);
</script>

#### Application with N users in 4 chat rooms - Time

<div width="400" height="400">
	<canvas id="compare-time-4" ></canvas>
</div>
<script>
	createChart("compare-time-4", 'Time in milliseconds', "avgTime", 4);
</script>

### CPU usage

> The CPU usage above 100% is due to the plurality of processors of the particular machine, which offers a **maximum of 800% CPU ** (8 cores of processing)

#### Application with N users in 1 chat room - CPU

<div width="400" height="400">
	<canvas id="compare-cpu-1" ></canvas>
</div>
<script>
	createChart("compare-cpu-1", '% of CPU', "avgCpuUse", 1);
</script>

#### Application with N users in 2 chat rooms - CPU

<div width="400" height="400">
	<canvas id="compare-cpu-2" ></canvas>
</div>
<script>
	createChart("compare-cpu-2", '% of CPU', "avgCpuUse", 2);
</script>

#### Application with N users in 4 chat rooms - CPU

<div width="400" height="400">
	<canvas id="compare-cpu-4" ></canvas>
</div>
<script>
	createChart("compare-cpu-4", '% of CPU', "avgCpuUse", 4);
</script>

### Memory usage

#### Application with N users in 1 chat room - Memory

<div width="400" height="400">
	<canvas id="compare-memory-1" ></canvas>
</div>
<script>
	createChart("compare-memory-1", '% of Memory', "avgMemoryUse", 1);
</script>

#### Application with N users in 2 chat rooms - Memory

<div width="400" height="400">
	<canvas id="compare-memory-2" ></canvas>
</div>
<script>
	createChart("compare-memory-2", '% of Memory', "avgMemoryUse", 2);
</script>


#### Application with N users in 4 chat rooms - Memory

<div width="400" height="400">
	<canvas id="compare-memory-4" ></canvas>
</div>
<script>
	createChart("compare-memory-4", '% of Memory', "avgMemoryUse", 4);
</script>

## Comparative study

### Response times

Spring applications, although with differences between them, offer considerably better results than the rest of technologies, followed by Akka.

On the other hand, in the applications of Vert.x, it can be seen that the use of the Eventbus assumes a greater consumption of time than if it is not used.

The worst results within this metric are found in the application of Node.js. This application is executed in a single thread, unlike the other technologies that make use of multiple threads to attend the requests concurrently. 

The Node.js application with the cluster library tries to solve this problem, improving response time, with worse results than Java applications, except Vert.x with Eventbus

Therefore, we can affirm that the best option is SpringBoot, that makes use of a server in Tomcat (which is configured by default).

### CPU usage

We can denote the correlation with the response times. Technologies that show better times (SpringBoot and Akka) also make more CPU use. 

In the case of Vert.x, the Eventbus not only harms the response time, also makes much greater use of the CPU.

Node.js applications, following the correlation mentioned, makes much less use of this resource. In the case of simple application, this is limited to a single processor, reaching in the comparative almost 100% use of it. On the other hand, the application that uses the cluster library, which uses multiple processors, distribute better the workload, making a more efficient use of resources.

### Memory usage

Java applications, for low workloads, consume a similar memory (between 9 and 12%), but when the workload increases (more than 40 users with any number of rooms), Vertx with Eventbus and Akka trigger their memory consumption (25 and 15% respectively).

The Vert.x application owes this excessive memory usage to its Eventbus, the same application without the use of this resource, has a constant memory usage, as do SpringBoot application. We can also see how the creation of actors by Akka also has repercussions on the use of memory.

On the other hand, we can see that the applications that use this resource are Node.js, which would be the best if we care this metric.

### Building

At the time of developing, we must also consider the time and/or difficulty that can lead us, in this case, to create a reactive system. 

Akka and Vert.x applications have extensive libraries that entails an initial learning curve that is much higher than the other technologies shown, introducing the model of actors in order to solve problems of concurrency. 
In the case of Akka, in addition, it is added the difficulty of embedding our application in the framework Play to obtain a Webscoket server.

On the other hand, SpringBoot applications are much simpler and faster to build through to its investment of control, although it leaves to the user's hands solve possible concurrency problems.

Finally, build reactive applications in Node.js is trival, given the reactive nature of the language itself, being able to write all the functionality in very few lines in a clear and concise way. However, when you add the cluster library, the flow of the application can be complicated.

## Conclusions

After studying the different metrics, we can state the following solutions to the problem of reactive applications:

* If we are looking for a reliable application **against large workloads** and do not make excessive use of the resources of the machine on which it runs, the optimal technology would be **SpringBoot**, specifically using as Tomcat server.

* If we are looking for a lightweight application that makes a **minimal use of the resources of the machine** that will not have large workloads, our best option would be **Node.js** (adding the cluster library if necessary to optimize the service it provides).
