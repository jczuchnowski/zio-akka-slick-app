package example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import example.application.ApplicationService
import example.domain._
import example.infrastructure._
import example.interop.akka.{ ErrorMapper, ZioSupport }
import spray.json._

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

class Api(env: SlickAssetRepository with SlickPortfolioAssetRepository, port: Int) extends JsonSupport with ZioSupport {

  lazy val route = assetRoute ~ portfolioRoute

  implicit val domainErrorMapper = new ErrorMapper[DomainError] {
    def toHttpResponse(e: DomainError): HttpResponse = e match {
      case RepositoryError(cause) => HttpResponse(StatusCodes.InternalServerError)
      case ValidationError(msg)   => HttpResponse(StatusCodes.BadRequest)
    }
  }

  val assetRoute =
    pathPrefix("assets") {
      pathEnd {
        get {
          complete(ApplicationService.getAssets.provide(env))
        } ~
        post {
          extractScheme { scheme =>
            extractHost { host => 
              entity(Directives.as[CreateAssetRequest]) { req =>
                ApplicationService.addAsset(req.name, req.price).provide(env).map { id =>
                  respondWithHeader(Location(Uri(scheme = scheme).withAuthority(host, port).withPath(Uri.Path(s"/assets/${id.value}")))) {
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
          entity(Directives.as[UpdateAssetRequest]) { req =>
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
          entity(Directives.as[UpdatePortfolioRequest]) { req =>
            complete(ApplicationService.updatePortfolio(PortfolioId(portfolioId), AssetId(req.assetId), req.amount).provide(env))
          }
        }
      } 
    }

}
