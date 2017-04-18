## Introducción 

La necesidad hoy en día de sistemas distribuidos altamente escalables no es ninguna novedad. Una forma de abordar esta necesidad puede ser mediante el uso de tecnologías reactivas, que siguiendo el paradigma reactivo cuentan entre sus características:

 - Tiempos de respuestas rapidos
 - Tolerantes a fallos 
 - Adaptación a variaciones en la carga de trabajo
 - Uso de mensajes asíncronos para la comunicación (no bloqueantes)

Aunque son muchas las tecnologías aplicables a este problema, las elegidas para esta comparativa son las siguientes:

<img class="logo" src="http://akka.io/resources/images/akka_full_color.svg" width="200">

**[Akka (v2.4)](http://http://akka.io/)**: librería nativa de Scala basada en el modelo de actores, aunque para este proyecto hemos usado su versión en Java. Para poder lanzar la aplicación en un servidor haremos uso de PlayFramework, el cual incluye la libreria de Akka además de librerias adicionales para la conexión mediante websocket.

> Aplicaciones en la comparativa:
>
>- **Akka + PlayFramework**

<img  class="logo" src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Vert.x_Logo.svg/2000px-Vert.x_Logo.svg.png" width="200" style="margin-left: 2em;">

**[Vertx (v3.3.3)](http://vertx.io/)**: librería disponible en diferentes lenguajes de programación para crear aplicaciones reactivas, también usaremos su versión en Java. Esta librería provee tanto un servidor para lanzar la aplicación como para websockets. Uno de sus principales (y cuestionados, como veremos mas adelante) recursos es su EventBus, siendo este el que le da su carácter reactivo. Compararemos como se comporta haciendo o no uso de el:
> Aplicaciones en la comparativa:
>
> - **Vertx con EventBus**
> - **Vertx sin EventBus**

<img  class="logo" src="https://nodejs.org/static/images/logos/nodejs-new-pantone-black.png" width="200">

**[Node.js(v5.6.0)](https://nodejs.org)**: entorno en tiempo de ejecución para Javascript, basado en una arquitectura de eventos. Node.js corre por defecto en único hilo de ejecución, aunque dispone de librerías para aprovechar todos los procesadores de la máquina que lo utilice. Contemplaremos ambas implementaciones haciendo uso de la librería Express para lanzar el servidor y ws para manejar la conexión mediante websocket.
> Aplicaciones en la comparativa:
> 
> - **Node.js + Express**
> - **Node.js + Express + Cluster**

<img  class="logo spring-logo" src="http://rubenjgarcia.es/wp-content/uploads/2016/09/springboot.png" width="200">

**[SpringBoot(v1.4.3)](https://projects.spring.io/spring-boot/)**: tecnología perteneciente al ecosistema de Spring (framework de Java para el desarrollo de aplicaciones). Puede utilizar distintos servidores para lanzar la aplicación, en esta comparativa probaremos:

> Aplicaciones en la comparativa:
> 
> -  **SpringBoot + Tomcat**
> -  **SpringBoot + Jetty**

El objetivo de esta comparativa será comprobar cómo se comportan estas tecnologías a medida que la carga de trabajo aumenta, concretamente prestaremos atención a los tiempos de respuesta y al uso de los recursos de la máquina (cómo aprovechan los cores y el uso de memoria).

Para ello, **se han desarrollado para cada tecnología un chat distribuido** de funcionamiento idéntico y **un cliente común** para que haya la máxima homogeneidad en las pruebas. 

El funcionamiento de este chat es sencillo, el cliente se conecta con un nombre de usuario (que se comprueba que sea único) y el nombre de una sala de chat a la que se une. De esta forma, el usuario que se conecta a la sala de chat y puede enviar mensajes y recibir mensajes del resto de usuarios conectados al mismo chat.

## Implementación

Las implementaciones de Akka y Vert.x corresponden a Javier Mateos, aunque las aplicaciones de Vert.x fueron revisadas y actualizadas de la versión 2.1.5 a la 3.3.3 de la misma librería. El código fuente de las aplicaciones utilizadas puede encontrarse en el siguiente repositorio, que corresponde a la segunda versión del proyecto (v2.0):

https://github.com/Maes95/TFG-WebChat/tree/v1.0

A pesar de que muchas tecnologías se apoyan en librerías externas para gestionar las comunicaciones con WebSocket (como Sockjs o Socket.io), se ha utilizado la implementación básica proporcionada por la tecnología en cuestión.

El cliente está desarrollado en Java y utiliza conjuntamente las librerías de testing JUnit y las librerías nativas de testing de Vert.x. Su implementación puede encontrarse en:

https://github.com/Maes95/TFG-WebChat/tree/v1.0/WebChatTest

## Comparativa

Las pruebas se realizarán en una máquina Linux Mint 17.3 con 8 cores. Estas pruebas consistirán en lanzar X clientes que enviarán 500 mensajes periódicamente al resto y almacena los que recibe en un array (para comprobar que llegan todos). Para medir el tiempo que tardan los mensajes en recibirse, el cliente que envía el mensaje adjunta en el cuerpo del mensaje el tiempo en ese momento además de un identificador. Al llegar el mensaje, se calcula el tiempo que ha tardado (tiempo actual menos el tiempo que trae en el cuerpo) y este se suma a una variable, que al llegar todos los mensajes se divide entre el número total de mensajes, obteniendo así el tiempo medio (en milisegundos) que tarda un mensaje en transmitirse.

Para el experimento, se han realizado pruebas para distinto número de chats en las diferentes aplicaciones. Para estas pruebas se ha probado el cliente con distintos números de usuarios (se han realizado hasta 10 iteraciones para cada número de usuarios para obtener la máxima homogeneidad en los resultados).

A continuación se muestran los resultados en función de 3 métricas: Tiempo de respuesta, uso de la CPU y consumo de memoria.

### Tiempo de respuesta

#### Aplicación con N usuarios en 1 sala de chat


<div width="400" height="400">
	<canvas id="compare-time-1" ></canvas>
</div>
<script>
	createChart("compare-time-1", 'Time in milliseconds', "avgTime", 1);
</script>


#### Aplicación con N usuarios en 2 salas de chat

<div width="400" height="400">
	<canvas id="compare-time-2" ></canvas>
</div>
<script>
	createChart("compare-time-2", 'Time in milliseconds', "avgTime", 2);
</script>


#### Aplicación con N usuarios en 4 salas de chat


<div width="400" height="400">
	<canvas id="compare-time-4" ></canvas>
</div>
<script>
	createChart("compare-time-4", 'Time in milliseconds', "avgTime", 4);
</script>


### Uso de la CPU 

> El uso de CPU por encima del 100% se debe a la pluralidad de procesadores de la máquina concreta, que ofrece un **máximo de 800% de CPU** (8 cores de procesamiento)

#### Aplicación con N usuarios en 1 sala de chat

<div width="400" height="400">
	<canvas id="compare-cpu-1" ></canvas>
</div>
<script>
	createChart("compare-cpu-1", '% of CPU', "avgCpuUse", 1);
</script>


#### Aplicación con N usuarios en 2 salas de chat


<div width="400" height="400">
	<canvas id="compare-cpu-2" ></canvas>
</div>
<script>
	createChart("compare-cpu-2", '% of CPU', "avgCpuUse", 2);
</script>


#### Aplicación con N usuarios en 4 salas de chat


<div width="400" height="400">
	<canvas id="compare-cpu-4" ></canvas>
</div>
<script>
	createChart("compare-cpu-4", '% of CPU', "avgCpuUse", 4);
</script>


### Consumo de memoria

#### Aplicación con N usuarios en 1 sala de chat


<div width="400" height="400">
	<canvas id="compare-memory-1" ></canvas>
</div>
<script>
	createChart("compare-memory-1", '% of Memory', "avgMemoryUse", 1);
</script>


#### Aplicación con N usuarios en 2 salas de chat


<div width="400" height="400">
	<canvas id="compare-memory-2" ></canvas>
</div>
<script>
	createChart("compare-memory-2", '% of Memory', "avgMemoryUse", 2);
</script>


#### Aplicación con N usuarios en 4 salas de chat


<div width="400" height="400">
	<canvas id="compare-memory-4" ></canvas>
</div>
<script>
	createChart("compare-memory-4", '% of Memory', "avgMemoryUse", 4);
</script>


## Estudio comparativo

### Tiempos de respuesta

Las aplicaciones de Spring, aunque con diferencias entre ellas, ofrecen resultados considerablemente mejores que el resto de tecnologías, seguidas por Akka. 

Por otro lado, en la aplicaciones de Vert.x, puede apreciarse que el uso del Eventbus supone un consumo de tiempo mayor que si no se hace uso de él.

Los peores resultados dentro de esta métrica los encontramos en la aplicación de Node.js. Dicha aplicación se ejecuta en un solo hilo de ejecución, a diferencia del resto de tecnologías que hacen uso de múltiples hilos para atender las peticiones de forma concurrente. La aplicación de Node.js junto a la libreria de cluster intenta dar solución a este problema, mejorando considerablemente el tiempo de respuesta, aunque quedando muy por detrás de las tecnologías que se ejecutan sobre Java, a excepción de Vert.x con Eventbus.

Podemos afirmar que atendiendo al tiempo de respuesta, la mejor opción seria una aplicación en SpringBoot haciendo uso de un servidor en Tomcat (el cual viene configurado por defecto). 

### Uso de CPU

En esta métrica, puede apreciarse la correlación con el tiempo de respuesta. Las tecnologías que ofrecían mejores tiempos (SpringBoot  y Akka), también hacen un uso mayor de CPU, destacando el gran consumo que genera Akka de este recurso.

En el caso de Vert.x, el Eventbus no sólo perjudica al tiempo de respuesta, si no que también hace un uso mucho mayor de la CPU.

Las aplicaciones de Node.js, siguiendo la correlación mencionada, hace un uso mucho menor de este recurso. En el caso de la aplicación simple, esta queda limitada a un solo procesador, alcanzando en las comparativas casi el 100% de uso del mismo, mientras que la aplicación que hace uso de la libreria de cluster, al hacer uso de múltiples procesadores, reparte mejor la carga de trabajo, haciendo un uso mas eficiente de los recursos y reduciendo en casi un 70% el uso del CPU respecto a su predecesora y posicionandose como la mejor tecnología en esta métrica.

### Uso de Memoria

Las aplicaciones de Java, para cargas de trabajo bajas, consumen una memoria similar (entre 9 y 12%), pero cuando la carga aumenta (mas de 40 usuarios con cualquier número de salas), las aplicaciones de Vertx con Eventbus y Akka disparan su consumo de memoria (25 y 15 % respectivamente). La aplicación de Vert.x debe este uso excesivo de memoria a su Eventbus, ya que la misma aplicación si el uso de este recurso, tiene un uso de memoria constante, al igual que las aplicaciones de SpringBoot . Tambien podemos observar como la creación de actores por parte de Akka también repercute en el uso de memoria.

Por otro lado, podemos observar que las aplicaciones que menos uso hacen de este recurso son las de Node.js, las cuales serían las óptimas si atendemos a esta métrica.

### Construcción

A la hora de desarrollar, también debemos plantearnos el tiempo y/o dificultad que nos puede entrañar, en este caso, crear un sistema reactivo. 

Tanto las aplicaciones de Akka y Vert.x disponen de amplias librerías que conlleva una curva de aprendiza inicial bastante mayor que el resto de tecnologías mostradas, introduciendo el modelo de actores de cara a resolver problemas de concurrencia. En el caso de Akka, además, se le añade la dificultad de incrustar nuestra aplicación en el framework Play para obtener un servidor Webscoket.

Por otro lado, las aplicaciones de SpringBoot son mucho mas sencillas y rápidas de construir gracias a su inversión de control, aunque deja en manos del usuario solventar posibles problemas de concurrencia.

Por último, las aplicaciones de Node.js para obtener un servicio reactivo resultan triviales dado el carácter reactivo del propio lenguaje, pudiendo escribir toda la funcionalidad en muy pocas lineas de forma clara y concisa, sin embargo, al añadir la librería de cluster, el flujo de la aplicación puede complicarse.

## Conclusiones

Tras estudiar las distintas métricas, podemos enunciar las siguiente soluciones al problema de las aplicaciones reactivas:

* Si buscamos una aplicación fiable ante **grandes cargas de trabajo** y que no haga un uso excesivo de los recursos de la máquina en la que se ejecuta, la tecnología óptima sería **SpringBoot**, concretamente usando como servidor Tomcat.

* Si buscamos una aplicación ligera, que haga un **uso mínimo de los recursos de la máquina** que no vaya a tener grandes cargas de trabajo, nuestra mejor opción seria **Node.js** (añadiendole la libreria de cluster si fuera necesaria para optimizar el servicio que proporciona). 
