name := "lpg"

version := "v0.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += jdbc
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test

libraryDependencies += "org.squeryl" % "squeryl_2.10.0" % "0.9.5-6"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.27"