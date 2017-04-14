## Introducción 
La necesidad hoy en día de sistemas distribuidos altamente escalables no es ninguna novedad. Una forma de abordar esta necesidad puede ser mediante el uso de tecnologías reactivas, que siguiendo el paradigma reactivo cuentan entre sus características:

 - Tiempos de respuestas rapidos
 - Tolerantes a fallos
 - Adaptación a variaciones en la carga de trabajo
 - Uso de mensajes asíncronos para la comunicación (no bloqueantes)

Aunque son muchas las tecnologías aplicables a este problema, las elegidas para esta comparativa son las siguientes:

<img src="http://akka.io/resources/images/akka_full_color.svg" width="200">

**[Akka (v2.4)](http://http://akka.io/)**: librería nativa de Scala basada en el modelo de actores, aunque para este proyecto hemos usado su versión en Java. Para poder lanzar la aplicación en un servidor haremos uso de PlayFramework, el cual incluye la libreria de Akka además de librerias adicionales para la conexión mediante websocket.

> Aplicaciones en la comparativa:
>
>- **Akka + PlayFramework**

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Vert.x_Logo.svg/2000px-Vert.x_Logo.svg.png" width="200">

**[Vertx (v3.3.3)](http://vertx.io/)**: librería disponible en diferentes lenguajes de programación para crear aplicaciones reactivas, también usaremos su versión en Java. Esta librería provee tanto un servidor para lanzar la aplicación como para websockets. Uno de sus principales (y cuestionados, como veremos mas adelante) recursos es su EventBus, siendo este el que le da su carácter reactivo. Compararemos como se comporta haciendo o no uso de el:
> Aplicaciones en la comparativa:
>
> - **Vertx con EventBus**
> - **Vertx sin EventBus**

<img src="https://nodejs.org/static/images/logos/nodejs-new-pantone-black.png" width="200">

**[Node.js(v5.6.0)](https://nodejs.org)**: entorno en tiempo de ejecución para Javascript, basado en una arquitectura de eventos. Node.js corre por defecto en único hilo de ejecución, aunque dispone de librerías para aprovechar todos los procesadores de la máquina que lo utilice. Contemplaremos ambas implementaciones haciendo uso de la librería Express para lanzar el servidor y ws para manejar la conexión mediante websocket.
> Aplicaciones en la comparativa:
>
> - **Node.js + Express**
> - **Node.js + Express + Cluster**

<space></space>
<img src="http://rubenjgarcia.es/wp-content/uploads/2016/09/springboot.png" width="200">
<space></space>
<space></space>

**[SpringBoot(v1.4.3)](https://projects.spring.io/spring-boot/)**: tecnología perteneciente al ecosistema de Spring (framework de Java para el desarrollo de aplicaciones). Puede utilizar distintos servidores para lanzar la aplicación, en esta comparativa probaremos:

> Aplicaciones en la comparativa:
>
> -  **SpringBoot + Tomcat**
> -  **SpringBoot + Jetty**

El objetivo de esta comparativa será comprobar cómo se comportan estas tecnologías a medida que la carga de trabajo aumenta, concretamente prestaremos atención a los tiempos de respuesta y al uso de los recursos de la máquina (cómo aprovechan los cores y el uso de memoria).

Para ello, **se han desarrollado para cada tecnología un chat distribuido** de funcionamiento idéntico y **un cliente común** para que haya la máxima homogeneidad en las pruebas. El cliente está desarrollado en Java y utiliza conjuntamente las librerías de testing JUnit y las librerías nativas de testing de Vert.x.

El funcionamiento de este chat es sencillo, el cliente se conecta con un nombre de usuario (que se comprueba que sea único) y el nombre de una sala de chat a la que se une. De esta forma, el usuario que se conecta a la sala de chat y puede enviar mensajes y recibir mensajes del resto de usuarios conectados al mismo chat.

## Implementación

Las implementaciones de Akka y Vert.x corresponden a Javier Mateos, aunque las aplicaciones de Vert.x fueron revisadas y actualizadas de la versión 2.1.5 a la 3.3.3 de la misma librería. El código fuente de las aplicaciones utilizadas puede encontrarse en el siguiente repositorio, que corresponde a la primera versión del proyecto (v1.0):

https://github.com/Maes95/TFG-WebChat/tree/v1.0
**TO DO -> ACTUALIZAR TAG**

A pesar de que muchas tecnologías se apoyan en librerías externas para gestionar las comunicaciones con WebSocket (como Sockjs o Socket.io), se ha utilizado la implementación básica proporcionada por la tecnología en cuestión.

**TO DO -> EXPLICAR COMO SE HIZO EL CLIENTE**

## Comparativa

Las pruebas se realizarán en una máquina Linux Mint 17.3 con 8 cores. Estas pruebas consistirán en lanzar X clientes que enviarán 500 mensajes periódicamente al resto y almacena los que recibe en un array (para comprobar que llegan todos). Para medir el tiempo que tardan los mensajes en recibirse, el cliente que envía el mensaje adjunta en el cuerpo del mensaje el tiempo en ese momento además de un identificador. Al llegar el mensaje, se calcula el tiempo que ha tardado (tiempo actual menos el tiempo que trae en el cuerpo) y este se suma a una variable, que al llegar todos los mensajes se divide entre el número total de mensajes, obteniendo así el tiempo medio (en milisegundos) que tarda un mensaje en transmitirse.

Para el experimento, se han realizado pruebas para distinto número de chats en las diferentes aplicaciones. Para estas pruebas se ha probado el cliente con distintos números de usuarios (se han realizado hasta 10 iteraciones para cada número de usuarios para obtener la máxima homogeneidad en los resultados).

### Tiempo de respuesta

#### Aplicación con N usuarios en 1 sala de chat

<div width="400" height="400">
<canvas id="compate-time" ></canvas>
</div>
<script>
	createChart("compate-time", 'Time in milliseconds', "avgTime", 1);
</script>

> **Nota:** Al correr la aplicación en Vert.x con 70 usuarios este daba un aviso de que los EventLoops (Threads en Vert.x) se quedan bloqueados demasiado tiempo. Dándole más tiempo de espera a estos Threads únicamente provoca que el GC (Garbage Collector) de Java se llenase y diese un error, por lo que no ha sido posible obtener resultados en este caso.

#### Aplicación con N usuarios en 2 salas de chat

![2 Rooms - Time](https://lh3.googleusercontent.com/-Uwlp0D3wJoY/WOu66Dw4_DI/AAAAAAAAAto/61Sc1fs2ODw0lxy6gHEnNKqpBMBBAKF-gCLcB/s0/N+users+in+2+chat+room%2528s%2529.png "N users in 2 chat room&#40;s&#41;.png")

#### Aplicación con N usuarios en 4 salas de chat

![enter image description here](https://lh3.googleusercontent.com/-ZDpS4eYUgSY/WOu7QPSk4nI/AAAAAAAAAtw/_RZahuFWw18XlzmoZe829UKy8E6iNRSfwCLcB/s0/N+users+in+4+chat+room%2528s%2529.png "N users in 4 chat room&#40;s&#41;.png")


### Uso de la CPU y consumo de memoria

Mediante el comando <code>top -p PID</code> de Linux hemos podido comprobar que uso le daba nuestras aplicaciones a los recursos del sistema. En concreto, los resultados corresponden al caso de 50 usuarios en una sola sala de chat.


## Conclusiones

Atendiendo a los tiempos de respuesta, Akka es la mejor opción, su intercambio de mensajes entre actores parece ser más eficiente que la comunicación mediante el eventbus de Vert.x o el sistema reactivo nativo de Node.js.

Además, podemos denotar como Node.js escala mejor que Vert.x cuando aumenta el número de usuarios en un chat único, aunque a medida que se aumenta el número de salas de chat, Vert.x se vuelve más parejo en su escalado a Akka mientras que Node.js no mejora en absoluto, continuando con su escalado lineal.

Si nos fijamos en el uso de la CPU, destaca cómo las aplicaciones que corren sobre la máquina virtual de Java aprovechan todos los cores de la máquina, al contrario que Node.js.

Cabe a destacar, que mientras Akka y Vert.x son tecnologías basadas en el modelo de actores y aprovechan varios hilos de ejecución para escalar, Node.js funciona con un solo hilo de ejecución (por lo que ahorra mucho tiempo en cambios de contexto). El problema de este modelo es que necesita módulos adicionales para poder aprovechar al máximo los núcleos del procesador (la aplicación en Node.js con la que se han realizado las pruebas no cuenta con ningún módulo que lo permita).

Por el contrario, atendiendo al uso de la memoria, Node.js hace un uso más eficiente de la misma (1,3%) frente a las aplicaciones en Java: Vert.x (18,9%) y Akka (10,3%). Esto se debe a que Node.js no hace uso de entidades como los verticles de Vertx o los actores de Akka.
