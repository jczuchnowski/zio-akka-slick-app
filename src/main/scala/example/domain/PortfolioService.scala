package example.domain

import scalaz.zio.{ IO, UIO, ZIO }

object PortfolioService {

  def calculatePortfolioStatus(portfolioId: PortfolioId): ZIO[AssetRepository with PortfolioAssetRepository, Exception, PortfolioStatus] = 
    for {
      portfolioAssets <- ZIO.accessM[PortfolioAssetRepository](_.portfolioAssetRepository.getByPortfolioId(portfolioId))
      assetIds        =  portfolioAssets.map(_.assetId).toSet
      assets          <- ZIO.accessM[AssetRepository](_.assetRepository.getByIds(assetIds))
      assetsMap       =  assets.map(a => a.id -> a).toMap
      total           =  portfolioAssets.foldLeft(BigDecimal(0)) { case (total, next) =>
                              next.amount * assetsMap(next.assetId).price + total
                            }        
    } yield PortfolioStatus(total)

}

