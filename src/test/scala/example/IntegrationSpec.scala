package example.application

import akka.http.scaladsl.testkit.ScalatestRouteTest
import example.{ Api, JsonSupport }
import example.domain.{ Asset, AssetId, PortfolioAsset, PortfolioId }
import example.infrastructure.{ SlickAssetRepository, SlickPortfolioAssetRepository }
import example.infrastructure.tables.{ AssetsTable, PortfolioAssetsTable }
import example.interop.slick.DatabaseProvider
import example.interop.slick.dbio._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.backend._
import slick.lifted.TableQuery
import zio.{ Runtime, ZIO }
import zio.internal.Platform

class IntegrationSpec extends AnyFlatSpec with Matchers with Runtime[Unit] with ScalatestRouteTest with JsonSupport {
  
  override val environment: Unit = Runtime.default.environment

  override val platform: Platform = Runtime.default.platform

  trait TestDatabaseProvider extends DatabaseProvider {
    override val databaseProvider = new DatabaseProvider.Service {
      override val db = ZIO.effectTotal(Database.forConfig("h2mem1"))
    }
  }

  class TestEnv extends SlickAssetRepository with SlickPortfolioAssetRepository with TestDatabaseProvider

  val testEnv = new TestEnv()
  val api = new Api(testEnv, 8080)


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
