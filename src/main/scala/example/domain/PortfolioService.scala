package example.domain

import zio.ZIO

object PortfolioService {

  def calculatePortfolioStatus(portfolioId: PortfolioId): ZIO[AssetRepository with PortfolioAssetRepository, DomainError, PortfolioStatus] = 
    for {
      portfolioAssets <- ZIO.accessM[PortfolioAssetRepository](_.portfolioAssetRepository.getByPortfolioId(portfolioId))
      assetIds        =  portfolioAssets.map(_.assetId).toSet
      assets          <- ZIO.accessM[AssetRepository](_.assetRepository.getByIds(assetIds))
      assetsMap       =  assets.map(a => a.id.get -> a).toMap
      total           =  portfolioAssets.foldLeft(BigDecimal(0)) { case (total, next) =>
                              next.amount * assetsMap(next.assetId).price + total
                            }        
    } yield PortfolioStatus(total)

  def topPortfolioForAsset(assetId: AssetId): ZIO[AssetRepository with PortfolioAssetRepository, DomainError, Option[BigDecimal]] = 
    for {
      portfolioAssets <- ZIO.accessM[PortfolioAssetRepository](_.portfolioAssetRepository.getByAssetId(assetId))
      assetOpt        <- ZIO.accessM[AssetRepository](_.assetRepository.getById(assetId))
    } yield assetOpt.map { asset => 
      val maxPortfolioAsset = portfolioAssets.maxBy(_.amount)
      maxPortfolioAsset.amount * asset.price
    }

}

