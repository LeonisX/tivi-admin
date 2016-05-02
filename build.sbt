name := """tivi-admin"""

version := "1.0"

scalaVersion := "2.11.8"

//Define the java version to use
//javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//Add Javafx8 library
//unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

libraryDependencies ++= Seq(
  // Uncomment to use Akka
  //"com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
  "junit"             % "junit"           % "4.12"  % "test",
  "com.novocode"      % "junit-interface" % "0.11"  % "test"
)
