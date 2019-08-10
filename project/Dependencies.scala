import sbt._

object Dependencies {

  val akkaVersion     = "2.5.19"
  val akkaHttpVersion = "10.1.8"
  val zioVersion      = "1.0.0-RC11-1"

  lazy val akkaHttp        = "com.typesafe.akka"       %% "akka-http"    % akkaHttpVersion
  lazy val akkaStream      = "com.typesafe.akka"       %% "akka-stream"  % akkaVersion
  lazy val sprayJson       = "com.typesafe.akka"       %% "akka-http-spray-json" % akkaHttpVersion
  lazy val scalazZio       = "dev.zio"                 %% "zio" % zioVersion
  lazy val scalazZioRS     = "dev.zio"                 %% "zio-streams" % zioVersion
  lazy val scalazZioIntRS  = "dev.zio"                 %% "zio-interop-reactivestreams" % "1.0.2.0-RC1"
  lazy val slick           = "com.typesafe.slick"      %% "slick"        % "3.3.0"
  lazy val akkaHttpTestkit = "com.typesafe.akka"       %% "akka-http-testkit" % akkaHttpVersion
  lazy val akkaTestkit     = "com.typesafe.akka"       %% "akka-testkit" % akkaVersion
  lazy val scalaTest       = "org.scalatest"           %% "scalatest"    % "3.0.5"
  lazy val mockito         = "org.mockito"             %  "mockito-core" % "2.27.0"
  lazy val h2              = "com.h2database"          % "h2"            % "1.4.199"
}