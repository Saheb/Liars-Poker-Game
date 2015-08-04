name := "lpg"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.squeryl" %% "squeryl" % "0.9.5-6"
)     

play.Project.playScalaSettings
