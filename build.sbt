name := "slick_workshop"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.github.tminglei" %% "slick-pg" % "0.15.4",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "org.postgresql" % "postgresql" % "42.1.4",
  "com.github.scopt" %% "scopt" % "3.7.0"
)
