name := "cornichon-gherkin"
version := "0.1.0-SNAPSHOT"

description := "Cornichon - Gherkin integration"
licenses := Seq("Apache License, ASL Version 2.0" → url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.12.3"
crossScalaVersions := Seq("2.11.11", "2.12.3")

libraryDependencies ++= Seq(
  "io.cucumber" % "gherkin" % "4.1.3",
  "com.github.agourlay" %% "cornichon" % "0.12.7"
)
