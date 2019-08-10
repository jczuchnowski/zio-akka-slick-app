package example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import example.application.ApplicationService
import example.domain.PortfolioStatus
import example.infrastructure._
import example.interop.slick.dbio._
import slick.driver.H2Driver.api._
import scala.io.StdIn
import zio.{ DefaultRuntime, ZIO }
import zio.blocking.Blocking
import example.infrastructure.tables.AssetsTable
import example.infrastructure.tables.PortfolioAssetsTable

object Boot extends App {

  val runtime = new DefaultRuntime() {}
  implicit val ec = runtime.Platform.executor.asEC
  
  implicit val system = ActorSystem(name = "zio-example-system", defaultExecutionContext = Some(ec))
  implicit val materializer = ActorMaterializer()

  class LiveEnv 
    extends SlickAssetRepository 
    with SlickPortfolioAssetRepository 
    with LiveDatabaseProvider
    with Blocking.Live

  val liveEnv = new LiveEnv
  val assets = TableQuery[AssetsTable.Assets]
  val portfolioAssets = TableQuery[PortfolioAssetsTable.PortfolioAssets]

  val api = new Api(liveEnv)

  val setup = {
    import slick.jdbc.H2Profile.api._
    DBIO.seq(
      (assets.schema ++ portfolioAssets.schema).create
    )
  }

  val setupIO = ZIO.fromDBIO(setup).provide(liveEnv)
  runtime.unsafeRun(setupIO)
 

  val bindingFuture = Http().bindAndHandle(api.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
