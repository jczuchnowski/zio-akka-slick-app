import sbt._

object Dependencies {

  val akkaVersion     = "2.6.1"
  val akkaHttpVersion = "10.1.11"
  val zioVersion      = "1.0.0-RC18"

  lazy val akkaHttp         = "com.typesafe.akka"  %% "akka-http"            % akkaHttpVersion
  lazy val akkaStream       = "com.typesafe.akka"  %% "akka-stream"          % akkaVersion
  lazy val sprayJson        = "com.typesafe.akka"  %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaSlf4j        = "com.typesafe.akka"  %% "akka-slf4j"           % akkaVersion
  lazy val scalazZio        = "dev.zio"            %% "zio"                  % zioVersion
  lazy val scalazZioRS      = "dev.zio"            %% "zio-streams"          % zioVersion
  lazy val scalazZioIntRS   = "dev.zio"            %% "zio-interop-reactivestreams" % "1.0.3.5-RC2"
  lazy val slick            = "com.typesafe.slick" %% "slick"                % "3.3.2"
  lazy val akkaHttpTestkit  = "com.typesafe.akka"  %% "akka-http-testkit"    % akkaHttpVersion
  lazy val akkaTestkit      = "com.typesafe.akka"  %% "akka-testkit"         % akkaVersion
  lazy val scalaTest        = "org.scalatest"      %% "scalatest"            % "3.1.0"
  lazy val scalaTestMockito = "org.scalatestplus"  %% "mockito-1-10"         % "3.2.0.0-M2"
  lazy val h2               = "com.h2database"     %  "h2"                   % "1.4.200"
  lazy val logback          = "ch.qos.logback"     % "logback-classic"       % "1.2.3"
}