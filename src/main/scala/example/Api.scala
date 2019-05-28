package example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import example.application.ApplicationService
import example.domain.PortfolioStatus
import example.infrastructure._
import example.interop.ZioSupport
import example.interop.slick.DatabaseProvider
//import scalaz.zio.{ App, ZIO }
import slick.driver.H2Driver.api._
import scala.io.StdIn
import scala.concurrent.Future

import scalaz.zio._

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import example.domain.{ Asset, AssetId, PortfolioId }
import spray.json._
import akka.actor.Status
import akka.http.scaladsl.model.headers.Location

case class CreateAssetRequest(name: String, price: BigDecimal)
case class UpdateAssetRequest(name: String, price: BigDecimal)
case class UpdatePortfolioRequest(assetId: Long, amount: BigDecimal)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object AssetIdFormat extends JsonFormat[AssetId] {
    def write(m: AssetId) = JsNumber(m.value)
    def read(json: JsValue) = json match {
      case JsNumber(n) => AssetId(n.longValue())
      case _ => deserializationError("Number expected")
    }
  }
  implicit val assetFormat = jsonFormat3(Asset)
  implicit val createAssetRequestFormat = jsonFormat2(CreateAssetRequest)
  implicit val portfolioStatusFormat = jsonFormat1(PortfolioStatus)
  implicit val updateAssetRequestFormat = jsonFormat2(UpdateAssetRequest)
  implicit val updatePortfolioRequestFormat = jsonFormat2(UpdatePortfolioRequest)
}

class Api(env: SlickAssetRepository with SlickPortfolioAssetRepository) extends JsonSupport with ZioSupport {

  lazy val route = assetRoute ~ portfolioRoute

  val assetRoute =
    pathPrefix("assets") {
      pathEnd {
        get {
          complete(ApplicationService.getAssets.provide(env))
        } ~
        post {
          extractScheme { scheme =>
            extractHost { host => 
              entity(as[CreateAssetRequest]) { req =>
                ApplicationService.addAsset(req.name, req.price).provide(env).map { id => 
                  respondWithHeader(Location(Uri(scheme = scheme).withHost(host).withPath(Uri.Path(s"assets/$id")))) {
                    complete {
                      HttpResponse(StatusCodes.Created)
                    }
                  }
                }
              }
            }
          }
        }
      } ~
      path(LongNumber) { assetId =>
        put {
          entity(as[UpdateAssetRequest]) { req =>
            complete(ApplicationService.updateAsset(AssetId(assetId), req.name, req.price).provide(env).map(_ => JsObject.empty))
          }
        }
      }
    }

  val portfolioRoute = 
    pathPrefix("portfolios" / LongNumber) { portfolioId =>
      pathEnd {
        get {
          complete(ApplicationService.getPortfolio(PortfolioId(portfolioId)).provide(env))
        }
      } ~
      path("assets") {
        put {
          entity(as[UpdatePortfolioRequest]) { req =>
            complete(ApplicationService.updatePortfolio(PortfolioId(portfolioId), AssetId(req.assetId), req.amount).provide(env))
          }
        }
      } 
    }

}
