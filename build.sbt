import scala.languageFeature.postfixOps

name := "scala-texas-holdem"

version := "0.1"

scalaVersion := "3.0.0-M2"

scalacOptions ++= Seq("-language:postfixOps")


libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test,
  "org.scalactic" %% "scalactic" % "3.3.0-SNAP3"
)

libraryDependencies += "commons-io" % "commons-io" % "2.6"