scalaVersion := "2.11.6"

val akkaVersion = "2.3.11"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies +=
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion

version := "0.0.1"

name := "mgr-benchmars"