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

    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[RepositoryException, Unit] = ???

    def getByPortfolioId(portfolioId: PortfolioId): IO[RepositoryException, List[PortfolioAsset]] = {
      val query = portfolioAssets.filter(_.portfolioId === portfolioId)

      ZIO.fromDBIO(query.result).provide(self)
        .map(_.toList)
        .refineOrDie {
          case e: Exception => new RepositoryException(e)
        }
    }

    def getByAssetId(assetId: AssetId): IO[RepositoryException, List[PortfolioAsset]] = ???
  }

}
