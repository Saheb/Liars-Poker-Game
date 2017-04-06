name := "lpg"

version := "v0.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += cache
libraryDependencies += ws

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.scala-lang.modules" %% "scala-async" % "0.9.6"
)
