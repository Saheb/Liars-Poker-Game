name := "lpg"

version := "v0.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += cache
libraryDependencies += ws

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.1"
