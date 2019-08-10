package example.domain

import zio.IO

trait PortfolioAssetRepository {
  
  def portfolioAssetRepository: PortfolioAssetRepository.Service

}

object PortfolioAssetRepository {
  
  trait Service {
  
    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[RepositoryException, Unit]

    def getByPortfolioId(portfolioId: PortfolioId): IO[RepositoryException, List[PortfolioAsset]]

    def getByAssetId(assetId: AssetId): IO[RepositoryException, List[PortfolioAsset]]
  }

}
