name := "dropbox.reactive"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.dropbox.core" % "dropbox-core-sdk" % "1.7.5",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe" % "config" % "1.0.2"
)
