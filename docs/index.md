---
layout: default
comments: true
---

## Introduction

Nowadays there are many applications that, based on [WebSockets](https://en.wikipedia.org/wiki/WebSocket), provide real-time communication. These applications aim to have low response times (functional requirements) and minimum resource consumption (non-functional requirements).

We have a multitude of technologies to address the development of this type of applications, but which is the best? Throughout the article we will compare different technologies (and different implementations of the same ones) to find the answer.

To carry out the comparative, we will create a chat application for each technology that must meet certain requirements, which can be found [here](https://github.com/Maes95/TFG-WebChat/wiki#what-are-the-requirements-for-an-application-to-be-tested). 

> The functionating of this chat is simple, the client connects with a username (which proves to be unique) and the name of a chat room to which it is attached. In this way, the user who connects to the chat room can send and receive messages from other users connected to the same chat.

Following these requirements, we will use a common client that tests each of the applications for metrics such as response times and resource usage.

Among the technologies selected for the comparative, we will distinguish between reactive and non-reactive applications:


### Reactive technologies

These technologies, which follow the [Reactive Manifesto](http://www.reactivemanifesto.org/), count among its characteristics

- **Responsive:** Responsive systems focus on providing rapid and consistent response times, respond in a timely manner if at all possible. 

-  **Resilient:** The system stays responsive in the face of failure. Resilience is achieved by replication, containment, isolation and delegation. The systems must be able to recover without compromising the integrity of the system.

- **Elastic:** The system stays responsive under a varying workload. This implies designs that have no contention points or central bottlenecks. 

- **Message Driven:** Reactive Systems rely on asynchronous message-passing to establish a boundary between components that ensures loose coupling, isolation and location transparency.  Communication is non-blocking.

![Reactive Manifesto](https://lh3.googleusercontent.com/-YjSNNBJvh5Y/WSTEL5O5BHI/AAAAAAAAA64/qFzWMtKLjEE7Xa_zEiXyP70mkbSlZ6qngCLcB/s0/Untitled+Diagram.png "ReactiveManifest.png")

</br></br>
The reactive technologies selected were:

<img class="logo" src="http://akka.io/resources/images/akka_full_color.svg" width="200">

**[Akka](http://http://akka.io/)**: Scala's native library based on the actors model, although for this project we have used its version in Java. In order to launch the application on a server, we will use [PlayFramework](https://www.playframework.com/), which includes the Akka library as well as additional libraries for the connection via WebSocket.


<img  class="logo" src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Vert.x_Logo.svg/2000px-Vert.x_Logo.svg.png" width="200">

**[Vertx](http://vertx.io/)**: Java framework to create reactive applications. It provides a server to launch the application and WebSockets. One of its main (and questioned, as we will see later) resources is its EventBus, which is the one that gives it its reactive character. We will compare how it behaves using it or not.

<img  class="logo" src="https://nodejs.org/static/images/logos/nodejs-new-pantone-black.png" width="200">

**[Node.js](https://nodejs.org)**: Run-time environment for Javascript, based on an event-driven architecture. Node.js runs by default in a single thread, although it has libraries to take advantage of all the processors of the machine that uses it (like [cluster](https://nodejs.org/api/cluster.html)). We will contemplate both implementations making use of the [Express](http://expressjs.com) library to launch the server and [ws library](https://github.com/websockets/ws) to handle the connection using WebSocket.

### Non-reactive technologies

We add non-reactive technologies to also compare paradigms and check the effectiveness of reactive technologies versus those that are not.

<img  class="logo spring-logo" src="http://rubenjgarcia.es/wp-content/uploads/2016/09/springboot.png" width="200">

**[SpringBoot](https://projects.spring.io/spring-boot/)**: Technology belonging to the [Spring](https://spring.io/) ecosystem (Java framework for application development). It can be used with different servers to launch the application, in this comparison we will use Tomcat and Jetty.

Applications in the comparison:

- **[Akka + PlayFramework](https://github.com/Maes95/TFG-WebChat/tree/v2.0/AkkaPlay-WebChat)**
- **[Vertx with EventBus](https://github.com/Maes95/TFG-WebChat/tree/v2.0/Vertx-WebChat)**
- **[Vertx without EventBus](https://github.com/Maes95/TFG-WebChat/tree/v2.0/VertxNoEventbus-WebChat)**
- **[Node.js + Express](https://github.com/Maes95/TFG-WebChat/tree/v2.0/NodeJS-WebChat)**
- **[Node.js + Express + Cluster](https://github.com/Maes95/TFG-WebChat/tree/v2.0/NodeJSCluster-WebChat)**
- **[SpringBoot + Tomcat](https://github.com/Maes95/TFG-WebChat/tree/v2.0/SpringBoot-Tomcat-WebChat)**
- **[SpringBoot + Jetty](https://github.com/Maes95/TFG-WebChat/tree/v2.0/SpringBoot-Jetty-WebChat)**

## Implementation

The source code of the applications used can be found in the following repository, which corresponds to the [second version of the project (v2.0)](https://github.com/Maes95/TFG-WebChat/tree/v2.0).

Although many technologies rely on external libraries to manage communications with WebSocket (such as SockJS or Socket.io), the basic implementation provided by each technology has been used.

The client is developed in Java and uses together with the JUnit testing libraries and the native Vert.x testing libraries. This implementation can be found [here](https://github.com/Maes95/TFG-WebChat/tree/v2.0/WebChatTest).

## Comparative

To perform the comparison, our test client will generate N users for a specific application, each of which will send 500 messages to the users that belong to their same chat room. 

The tests have been performed for a different number of rooms in different applications. For these tests, the client has been tested with different numbers of users (up to 10 iterations to have been performed for each number of users to obtain the maximum homogeneity in the results).

The comparison has been made on a Linux Mint 17.3 machine with 8 CPU cores.

For this comparison we will consider 3 different metrics:

- **Latency (response time)**: To measure the time it takes for messages to be received, the client sending, attached to the message body, the current time and an identifier. When the message arrives, it calculates the time it has taken (current time less the time that it brings in the body) and this is added to a variable, that when arriving all the messages is divided between the total number of messages, obtaining the average time (in milliseconds) that a message takes to transmit.
</BR>
![latency](https://lh3.googleusercontent.com/Z2isjzBwNsI8dW9l9RWaZhODZbrGyZBXL3P1zZIycnzLxNLvRRhD2a1GZ_e4ryzWDqC28KGA=s0 "latency.png")
</BR>

- **Resources: CPU use and Memory**: Each second, the client collects the data provided by the `top` command of the particular application being tested, obtaining the percentage of CPU usage and the amount of physical RAM used by the process.


The results obtained from the test are shown below

- Graphs are dynamics, allow show-hide to compare concrete technologies
- The number of messages on the X axis corresponds to the total number of messages sent using the following equation: 

	`No. of messages = (No. of users/room ^ 2) * number of rooms`

- You can find the complete and disaggregated results of the comparative [here](http://tfg-dashboard.maes.gq/?view)

### Latency

#### Application with N users in 1 chat room - Latency

<div width="400" height="400">
	<canvas id="compare-time-1" ></canvas>
</div>
<script>
	createChart("compare-time-1", 'Time in milliseconds', "avgTime", 1);
</script>

#### Application with N users in 2 chat rooms - Latency

<div width="400" height="400">
	<canvas id="compare-time-2" ></canvas>
</div>
<script>
	createChart("compare-time-2", 'Time in milliseconds', "avgTime", 2);
</script>

#### Application with N users in 4 chat rooms - Latency

![4 Rooms - Time](https://lh3.googleusercontent.com/ZAENSkfnTRD8Myq_piJc8eS3oz-AVZEiyolzJlFFLTk4pe45slR6yTMjQnRvzb-VMbsxo0rx=s0 "N users in 4 chat room&#40;s&#41; - Time")

<div width="400" height="400">
	<canvas id="compare-time-4" ></canvas>
</div>
<script>
	createChart("compare-time-4", 'Time in milliseconds', "avgTime", 4);
</script>

Spring applications, although with differences between them, offer considerably better results than the rest of technologies, followed by Akka.

On the other hand, in the applications of Vert.x, it can be seen that the use of the Eventbus assumes a greater consumption of time than if it is not used.

The worst results within this metric are found in the application of Node.js. This application is executed in a single thread, unlike the other technologies that make use of multiple threads to attend the requests concurrently. 

The Node.js application with the cluster library tries to solve this problem, improving response time, with worse results than Java applications, except Vert.x with Eventbus

Therefore, we can affirm that the best option is SpringBoot, that makes use of a server in Tomcat (which is configured by default).

### CPU usage

> The CPU usage above 100% is due to the plurality of processors of the particular machine, which offers a **maximum of 800% CPU ** (8 cores of processing)

#### Application with N users in 1 chat room - CPU

![1 Room - CPU](https://lh3.googleusercontent.com/kUIeP_WDFautqInqn5v6s-gSqt8PSSalgFUBxBYPux6VtY5ET472M63y3n_uZcrlRVvcvGLZ=s0 "N users in 1 chat room&#40;s&#41; - CPU.png")

<div width="400" height="400">
	<canvas id="compare-cpu-1" ></canvas>
</div>
<script>
	createChart("compare-cpu-1", '% of CPU', "avgCpuUse", 1);
</script>

#### Application with N users in 2 chat rooms - CPU

![2 Rooms - CPU](https://lh3.googleusercontent.com/Q-Xa5AQxBzoNzLMj3MZ9-fBAfmqCQOTZwkuqJoinavjRIT7PATZtHtXMmF6MKMWOYS7MswCg=s0 "N users in 2 chat room&#40;s&#41; - CPU.png")

<div width="400" height="400">
	<canvas id="compare-cpu-2" ></canvas>
</div>
<script>
	createChart("compare-cpu-2", '% of CPU', "avgCpuUse", 2);
</script>

#### Application with N users in 4 chat rooms - CPU

![4 Rooms - CPU](https://lh3.googleusercontent.com/LmDJ2nVgztNgUw492EAg55nzRwVkTFYEdmuZ390TJydo5tamuFbffVZ98fqAKNPl7koKn_MH=s0 "N users in 4 chat room&#40;s&#41; - CPU.png")

<div width="400" height="400">
	<canvas id="compare-cpu-4" ></canvas>
</div>
<script>
	createChart("compare-cpu-4", '% of CPU', "avgCpuUse", 4);
</script>

We can denote the correlation with the response times. Technologies that show better times (SpringBoot and Akka) also make more CPU use. 

In the case of Vert.x, the Eventbus not only harms the response time, also makes much greater use of the CPU.

Node.js applications, following the correlation mentioned, makes much less use of this resource. In the case of simple application, this is limited to a single processor, reaching in the comparative almost 100% use of it. On the other hand, the application that uses the cluster library, which uses multiple processors, distribute better the workload, making a more efficient use of resources.


### Memory usage

> The total memory available on the machine running the test is `5.994.856 kB`

#### Application with N users in 1 chat room - Memory

<div width="400" height="400">
	<canvas id="compare-memory-1" ></canvas>
</div>
<script>
	createChart("compare-memory-1", 'Memory in KBytes', "avgRam", 1);
</script>

#### Application with N users in 2 chat rooms - Memory

<div width="400" height="400">
	<canvas id="compare-memory-2" ></canvas>
</div>
<script>
	createChart("compare-memory-2", 'Memory in KBytes', "avgRam", 2);
</script>

#### Application with N users in 4 chat rooms - Memory


<div width="400" height="400">
	<canvas id="compare-memory-4" ></canvas>
</div>
<script>
	createChart("compare-memory-4", 'Memory in KBytes', "avgRam", 4);
</script>

Java applications, for low workloads, consume a similar memory (between 9 and 12%), but when the workload increases (more than 40 users with any number of rooms), Vertx with Eventbus and Akka trigger their memory consumption (25 and 15% respectively).

The Vert.x application owes this excessive memory usage to its Eventbus, the same application without the use of this resource, has a constant memory usage, as do SpringBoot application. We can also see how the creation of actors by Akka also has repercussions on the use of memory.

On the other hand, we can see that the applications that use this resource are Node.js, which would be the best if we care this metric.

### Development

At the time of developing, we must also consider the time and/or difficulty that can lead us, in this case, to create a reactive system. 

Akka and Vert.x applications have extensive libraries that entails an initial learning curve that is much higher than the other technologies shown, introducing the model of actors in order to solve problems of concurrency. 
In the case of Akka, in addition, it is added the difficulty of embedding our application in the framework Play to obtain a Webscoket server.

On the other hand, SpringBoot applications are much simpler and faster to build through to its investment of control, although it leaves the user's hands solve possible concurrency problems.

Finally, build reactive applications in Node.js is trivial, given the reactive nature of the language itself, being able to write all the functionality in very few lines in a clear and concise way. However, when you add the cluster library, the flow of the application can be complicated.

