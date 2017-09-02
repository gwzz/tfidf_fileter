import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.wei",
      scalaVersion := "2.11.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "TfIdf",
    libraryDependencies += scalaTest % Test,
    // libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0",
    libraryDependencies ++= Seq(
		  "org.apache.spark"  %% "spark-core" % "2.1.0",
		  "org.apache.spark"  %% "spark-mllib" % "2.1.0"
		  )

  )
