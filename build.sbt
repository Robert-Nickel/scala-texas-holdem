name := "scala-texas-holdem"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"

coverageExcludedPackages := ".*Main.*"
