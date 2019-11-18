package example.infrastructure.tables

import example.domain._
import example.infrastructure.EntityIdMappers._
import slick.jdbc.H2Profile.api._

object AssetsTable {

  case class LiftedAsset(id: Rep[Option[AssetId]], name: Rep[String], price: Rep[BigDecimal])
  
  implicit object AssetShape extends CaseClassShape(LiftedAsset.tupled, Asset.tupled)
  
  class Assets(tag: Tag) extends Table[Asset](tag, "ASSETS") {
    def id = column[AssetId]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def price = column[BigDecimal]("PRICE")
    def * = LiftedAsset(id.?, name, price)
  }

}
