import io.apigee.trireme.node10.node.cluster

name := """play-java"""

version := "1.0-SNAPSHOT"

//val akkaVersion = "2.3.9"
val akkaVersion = "2.4-M2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
scalaVersion := "2.11.6"
libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % akkaVersion
  //"io.kamon" % "sigar-loader" % "1.6.5-rev001"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := false