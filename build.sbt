import scala.languageFeature.postfixOps

name := "scala-texas-holdem"

version := "0.1"

scalaVersion := "3.0.0-M2"

scalacOptions ++= Seq("-language:postfixOps")

libraryDependencies += "org.scalactic" % "scalactic_2.13" % "3.2.2"
libraryDependencies += "org.scalatest" % "scalatest_2.13" % "3.2.2" % "test"

coverageExcludedPackages := ".*Main.*"
