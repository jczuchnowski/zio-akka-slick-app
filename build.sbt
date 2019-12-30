import Dependencies._

ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

//-XX:MetaspaceSize=
scalacOptions ++= Seq("-Ypartial-unification", "-deprecation", "-feature", "-Ywarn-unused:imports")

lazy val root = (project in file("."))
  .settings(
    name := "zio-example",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
      sprayJson,
      scalazZio,
      scalazZioRS,
      scalazZioIntRS,
      slick,
      h2,
      scalaTest % Test,
      scalaTestMockito % Test,
      akkaTestkit % Test,
      akkaHttpTestkit % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
