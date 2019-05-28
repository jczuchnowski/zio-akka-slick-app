package example.infrastructure

import slick.driver.H2Driver.api._
import scalaz.zio.{ IO, ZIO }
import example.domain._
import example.infrastructure.EntityIdMappers._
import example.infrastructure.tables.AssetsTable
import example.interop.slick.dbio._
import example.interop.slick.DatabaseProvider

trait SlickAssetRepository extends AssetRepository with DatabaseProvider { self =>

  val assets = TableQuery[AssetsTable.Assets]

  val assetRepository = new AssetRepository.Service {

    def add(name: String, price: BigDecimal): IO[Exception, AssetId] = ???

    val getAll: IO[Exception, List[Asset]] = 
      ZIO.fromDBIO(assets.result).provide(self).map(_.toList).refineOrDie {
        case e: Exception => e
      }
    
    def getByName(name: String): IO[Exception, Option[Asset]] = ???

    def getById(id: AssetId): IO[Exception, Option[Asset]] = ???

    def getByIds(ids: Set[AssetId]): IO[Exception, List[Asset]] = {
      val query = assets.filter(_.id inSet ids)
      
      ZIO.fromDBIO(query.result).provide(self).map(_.toList).refineOrDie {
        case e: Exception => e
      }
    }

    def update(id: AssetId, name: String, price: BigDecimal): IO[Exception, Unit] = ???
  }

}
