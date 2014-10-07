import org.rbayer.GruntSbtPlugin._
import GruntKeys._

name := "todo-gkit-angularjs"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "org.specs2"    %% "specs2"               % "2.3.12-scalaz-7.1.0-M7" % "test",
  "org.scalaz"    %% "scalaz-core"          % "7.1.0-M7",
  "com.chuusai"   %% "shapeless"            % "2.0.0",
  "com.kindleit"  %% "play-gresource-mongo" % "0.2.0")

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

gruntSettings

lazy val root = (project in file(".")).enablePlugins(PlayScala)
