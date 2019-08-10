package example.application

import example.domain._
import zio.ZIO

object ApplicationService {

  def addAsset(name: String, price: BigDecimal): ZIO[AssetRepository, Exception, AssetId] =
    ZIO.accessM[AssetRepository](_.assetRepository.add(name, price))

  def updateAsset(assetId: AssetId, name: String, price: BigDecimal): ZIO[AssetRepository, Exception, Unit] =
    for {
      _ <- ZIO.accessM[AssetRepository](_.assetRepository.update(assetId, name, price))
    } yield ()

  val getAssets: ZIO[AssetRepository, Exception, List[Asset]] =
    ZIO.accessM[AssetRepository](_.assetRepository.getAll)

  def getPortfolio(portfolioId: PortfolioId): ZIO[AssetRepository with PortfolioAssetRepository, Exception, PortfolioStatus] =
    PortfolioService.calculatePortfolioStatus(portfolioId)

  def updatePortfolio(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): ZIO[AssetRepository with PortfolioAssetRepository, Exception, PortfolioStatus] =
    for {
      _ <- ZIO.accessM[PortfolioAssetRepository](_.portfolioAssetRepository.add(portfolioId, assetId, amount))
      p <- PortfolioService.calculatePortfolioStatus(portfolioId)
    } yield p

}
