import sbt._

object Dependencies {

  val akkaVersion     = "2.5.19"
  val akkaHttpVersion = "10.1.8"
  val zioVersion      = "1.0-RC3"

  lazy val akkaHttp        = "com.typesafe.akka"       %% "akka-http"    % akkaHttpVersion
  lazy val akkaStream      = "com.typesafe.akka"       %% "akka-stream"  % akkaVersion
  lazy val sprayJson       = "com.typesafe.akka"       %% "akka-http-spray-json" % akkaHttpVersion
  lazy val scalazZio       = "org.scalaz"              %% "scalaz-zio"   % zioVersion
  lazy val scalazZioRS     = "org.scalaz"              %% "scalaz-zio-interop-reactivestreams" % zioVersion
  lazy val slick           = "com.typesafe.slick"      %% "slick"        % "3.3.0"
  lazy val akkaHttpTestkit = "com.typesafe.akka"       %% "akka-http-testkit" % akkaHttpVersion
  lazy val akkaTestkit     = "com.typesafe.akka"       %% "akka-testkit" % akkaVersion
  lazy val scalaTest       = "org.scalatest"           %% "scalatest"    % "3.0.5"
  lazy val mockito         = "org.mockito"             %  "mockito-core" % "2.27.0"
  lazy val h2              = "com.h2database"          % "h2"            % "1.4.199"
}