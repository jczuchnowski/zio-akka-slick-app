package example.infrastructure

import slick.driver.H2Driver.api._
import zio.{ IO, ZIO }
import example.domain._
import example.infrastructure.EntityIdMappers._
import example.infrastructure.tables.PortfolioAssetsTable
import example.interop.slick.dbio._
import example.interop.slick.DatabaseProvider

trait SlickPortfolioAssetRepository extends PortfolioAssetRepository with DatabaseProvider { self =>

  val portfolioAssets = TableQuery[PortfolioAssetsTable.PortfolioAssets]

  val portfolioAssetRepository = new PortfolioAssetRepository.Service {

    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[RepositoryFailure, Unit] = ???

    def getByPortfolioId(portfolioId: PortfolioId): IO[RepositoryFailure, List[PortfolioAsset]] = {
      val query = portfolioAssets.filter(_.portfolioId === portfolioId)

      ZIO.fromDBIO(query.result).provide(self)
        .map(_.toList)
        .refineOrDie {
          case e: Exception => new RepositoryFailure(e)
        }
    }

    def getByAssetId(assetId: AssetId): IO[RepositoryFailure, List[PortfolioAsset]] = ???
  }

}
