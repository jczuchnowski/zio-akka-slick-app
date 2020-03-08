package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import example.infrastructure._
import example.interop.slick.dbio._
import slick.driver.H2Driver.api._
import scala.io.StdIn
import zio.{ Runtime, ZIO }
import example.infrastructure.tables.AssetsTable
import example.infrastructure.tables.PortfolioAssetsTable

object Boot extends App {

  
  implicit val system = ActorSystem(name = "zio-example-system")
  implicit val ec = system.dispatcher

  class LiveEnv 
    extends SlickAssetRepository 
    with SlickPortfolioAssetRepository 
    with LiveDatabaseProvider

  val liveEnv = new LiveEnv
  val assets = TableQuery[AssetsTable.Assets]
  val portfolioAssets = TableQuery[PortfolioAssetsTable.PortfolioAssets]

  val host = "localhost"
  val port = 8080

  val api = new Api(liveEnv, port)

  val setup = {
    import slick.jdbc.H2Profile.api._
    DBIO.seq(
      (assets.schema ++ portfolioAssets.schema).create
    )
  }

  val setupIO = ZIO.fromDBIO(setup).provide(liveEnv)
  Runtime.default.unsafeRun(setupIO)
 

  val bindingFuture = Http().bindAndHandle(api.route, host, port)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
