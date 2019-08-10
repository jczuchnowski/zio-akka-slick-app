package example.domain

import zio.IO

trait PortfolioAssetRepository {
  
  def portfolioAssetRepository: PortfolioAssetRepository.Service

}

object PortfolioAssetRepository {
  
  trait Service {
  
    def add(portfolioId: PortfolioId, assetId: AssetId, amount: BigDecimal): IO[RepositoryFailure, Unit]

    def getByPortfolioId(portfolioId: PortfolioId): IO[RepositoryFailure, List[PortfolioAsset]]

    def getByAssetId(assetId: AssetId): IO[RepositoryFailure, List[PortfolioAsset]]
  }

}
