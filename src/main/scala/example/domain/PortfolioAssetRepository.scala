package example.domain

import scalaz.zio.IO

trait PortfolioAssetRepository {
  
  def portfolioAssetRepository: PortfolioAssetRepository.Service

}

object PortfolioAssetRepository {
  
  trait Service {
  
    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[Exception, Unit]

    def getByPortfolioId(portfolioId: PortfolioId): IO[Exception, List[PortfolioAsset]]

  }

}
