package example.application

import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import example.Api
import example.domain.PortfolioAssetRepository
import example.domain.PortfolioId
import example.domain.PortfolioAsset
import example.domain.{ Asset, AssetId, AssetRepository }
import example.infrastructure.{ SlickAssetRepository, SlickPortfolioAssetRepository }
import example.infrastructure.tables.{ AssetsTable, PortfolioAssetsTable }
import example.interop.slick.DatabaseProvider
import example.interop.slick.dbio._
import org.json4s.{ DefaultFormats, jackson }
import org.mockito.Mockito
import org.scalatest._
import zio.{ IO, ZIO }
import zio.DefaultRuntime
import slick.jdbc.H2Profile.backend._
import slick.lifted.TableQuery

class IntegrationSpec extends FlatSpec with Matchers with DefaultRuntime with ScalatestRouteTest {
  
  implicit val serialization = jackson.Serialization
  implicit val formats       = DefaultFormats

  trait TestDatabaseProvider extends DatabaseProvider {
    override val databaseProvider = new DatabaseProvider.Service {
      override val db = ZIO.effectTotal(Database.forConfig("h2mem1"))
    }
  }

  class TestEnv extends SlickAssetRepository with SlickPortfolioAssetRepository with TestDatabaseProvider

  val testEnv = new TestEnv()
  val api = new Api(testEnv)


  val assets = TableQuery[AssetsTable.Assets]
  val portfolioAssets = TableQuery[PortfolioAssetsTable.PortfolioAssets]

  val setup = {
    import slick.jdbc.H2Profile.api._
    DBIO.seq(
      (assets.schema ++ portfolioAssets.schema).create,
      assets += Asset(Some(AssetId(1)), "GBPUSD", BigDecimal(100.0)),
      portfolioAssets += PortfolioAsset(PortfolioId(1), AssetId(1), 10)
    )
  }

  val setupIO = ZIO.fromDBIO(setup).provide(testEnv)
  this.unsafeRun(setupIO)
  
  "Assets endpoint" should "return a all assets for GET requests to the /assets path" in {
    Get("/assets") ~> api.route ~> check {
      responseAs[List[Asset]] shouldEqual List(Asset(Some(AssetId(1)), "GBPUSD", BigDecimal(100.0)))
    }
  }

}
