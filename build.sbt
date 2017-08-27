name := "cornichon-gherkin"
organization := "com.github.olegilyenko"
version := "0.1.0-SNAPSHOT"

description := "Cornichon - Gherkin integration"
homepage := Some(url("https://github.com/OlegIlyenko/cornichon-gherkin"))
licenses := Seq("Apache License, ASL Version 2.0" → url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.12.3"
crossScalaVersions := Seq("2.11.11", "2.12.3")

libraryDependencies ++= Seq(
  "io.cucumber" % "gherkin" % "4.1.3",
  "com.github.agourlay" %% "cornichon" % "0.12.7"
)

// Publishing

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := (_ ⇒ false)
publishTo := Some(
  if (version.value.trim.endsWith("SNAPSHOT"))
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

startYear := Some(2017)
organizationHomepage := Some(url("https://github.com/OlegIlyenko/cornichon-gherkin"))
developers := Developer("OlegIlyenko", "Oleg Ilyenko", "", url("https://github.com/OlegIlyenko")) :: Nil
scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/OlegIlyenko/cornichon-gherkin.git"),
  connection = "scm:git:git@github.com/OlegIlyenko/cornichon-gherkin.git"
))

// nice *magenta* prompt!

shellPrompt in ThisBuild := { state ⇒
  scala.Console.GREEN + Project.extract(state).currentRef.project + "> " + scala.Console.RESET
}
