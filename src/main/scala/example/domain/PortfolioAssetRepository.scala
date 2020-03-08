package example.domain

import zio.IO

trait PortfolioAssetRepository {
  
  def portfolioAssetRepository: PortfolioAssetRepository.Service

}

object PortfolioAssetRepository {
  
  trait Service {
  
    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[RepositoryError, Unit]

    def getByPortfolioId(portfolioId: PortfolioId): IO[RepositoryError, List[PortfolioAsset]]

    def getByAssetId(assetId: AssetId): IO[RepositoryError, List[PortfolioAsset]]
  }

}
