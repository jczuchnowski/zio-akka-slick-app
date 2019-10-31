package example.infrastructure

import example.domain._
import example.infrastructure.EntityIdMappers._
import example.infrastructure.tables.PortfolioAssetsTable
import example.interop.slick.dbio._
import example.interop.slick.DatabaseProvider
import slick.jdbc.H2Profile.api._
import zio.{ IO, ZIO }

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
