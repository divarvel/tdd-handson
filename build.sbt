name := "scala"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "io.spray" %% "spray-routing" % "1.3.2"

libraryDependencies += "io.spray" %% "spray-can" % "1.3.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.9"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0-M2"

scalacOptions ++= Seq(
  "-feature", "-unchecked", "-Xlint", "-Xlint", "-Ywarn-inaccessible",
  "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Ywarn-numeric-widen",
  "-Ywarn-value-discard", "-language:postfixOps"
)
