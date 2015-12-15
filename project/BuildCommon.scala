import sbt._

object BuildCommon {
	def version = "1.0-SNAPSHOT"
	def scalaVersion = "2.11.7"
	def scalacOption= Seq("-language:higherKinds",
            "-encoding", "UTF-8",
            "-Xlint",
            "-deprecation",
            "-Xfatal-warnings",
            "-feature",
            "-language:postfixOps",
            "-unchecked",
            "-language:implicitConversions", 
            "-target:jvm-1.6")
	def javacOptions =  Seq("-source", "1.6", "-target", "1.6")
}
