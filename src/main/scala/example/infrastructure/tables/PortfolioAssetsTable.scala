package example.infrastructure.tables

import example.domain._
import example.infrastructure.EntityIdMappers._
import slick.jdbc.H2Profile.api._

object PortfolioAssetsTable {

  case class LiftedPortfolioAsset(portfolioId: Rep[PortfolioId], assetId: Rep[AssetId], amount: Rep[BigDecimal])
  
  implicit object PortfolioAssetShape extends CaseClassShape(LiftedPortfolioAsset.tupled, PortfolioAsset.tupled)
  
  class PortfolioAssets(tag: Tag) extends Table[PortfolioAsset](tag, "portfolio_assets") {
    def portfolioId = column[PortfolioId]("PORTFOLIO_ID")
    def assetId = column[AssetId]("ASSET_ID")
    def amount = column[BigDecimal]("AMOUNT")

    def pk = primaryKey("pk_portfolio_id_asset_id", (portfolioId, assetId))

    def * = LiftedPortfolioAsset(portfolioId, assetId, amount)
  }

}
