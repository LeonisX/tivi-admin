name := """tivi-admin"""

version := "1.0"

scalaVersion := "2.11.8"

//Define the java version to use
//javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//Add Javafx8 library
//unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

libraryDependencies += "org.jsoup" % "jsoup" % "1.9.1"
libraryDependencies += "com.google.code.gson" % "gson" % "2.6.2"
