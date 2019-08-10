package example.infrastructure.tables

import slick.driver.H2Driver.api._
import example.domain._
import example.infrastructure.EntityIdMappers._

object AssetsTable {

  case class LiftedAsset(id: Rep[Option[AssetId]], name: Rep[String], price: Rep[BigDecimal])
  
  implicit object AssetShape extends CaseClassShape(LiftedAsset.tupled, Asset.tupled)
  
  class Assets(tag: Tag) extends Table[Asset](tag, "ASSETS") {
    def id = column[AssetId]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def price = column[BigDecimal]("PRICE")
    def * = LiftedAsset(id, name, price)
  }

}
