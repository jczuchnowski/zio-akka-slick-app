import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

//-XX:MetaspaceSize=

lazy val root = (project in file("."))
  .settings(
    name := "zio-example",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
      sprayJson,
      scalazZio,
      scalazZioRS,
      slick,
      h2,
      scalaTest % Test,
      akkaTestkit % Test,
      akkaHttpTestkit % Test,
      mockito % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
