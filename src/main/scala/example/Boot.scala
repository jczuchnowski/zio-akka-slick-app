package example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import example.application.ApplicationService
import example.domain.PortfolioStatus
import example.infrastructure._
import example.interop.slick.DatabaseProvider
//import scalaz.zio.{ App, ZIO }
import slick.driver.H2Driver.api._
import scala.io.StdIn


// object Hello extends App {
//   def run(args: List[String]) =
//     ApplicationService.updatePortfolio(1, 1, 2)
// }

object Boot extends App {

  implicit val system = ActorSystem("zio-example-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  class LiveEnv 
    extends SlickAssetRepository 
    with SlickPortfolioAssetRepository 
    with LiveDatabaseProvider

  val api = new Api(new LiveEnv)

  val bindingFuture = Http().bindAndHandle(api.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
