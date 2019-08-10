package example.infrastructure

import slick.driver.H2Driver.api._
import zio.{ IO, ZIO }
import example.domain._
import example.infrastructure.EntityIdMappers._
import example.infrastructure.tables.AssetsTable
import example.interop.slick.dbio._
import example.interop.slick.DatabaseProvider

trait SlickAssetRepository extends AssetRepository with DatabaseProvider { self =>

  val assets = TableQuery[AssetsTable.Assets]

  val assetRepository = new AssetRepository.Service {

    def add(name: String, price: BigDecimal): IO[RepositoryFailure, AssetId] = {
      val insert = (assets returning assets.map(_.id)) += Asset(None, name, price)
      ZIO.fromDBIO(insert).provide(self).refineOrDie {
        case e: Exception => new RepositoryFailure(e)
      }
    }

    val getAll: IO[RepositoryFailure, List[Asset]] = 
      ZIO.fromDBIO(assets.result).provide(self).map(_.toList).refineOrDie {
        case e: RepositoryFailure => e
      }

    def getByName(name: String): IO[RepositoryFailure, Option[Asset]] = {
      val query = assets.filter(_.name === name)

      ZIO.fromDBIO(query.result).provide(self).map(_.headOption).refineOrDie {
        case e: Exception => new RepositoryFailure(e)
      }      
    }

    def getById(id: AssetId): IO[RepositoryFailure, Option[Asset]] = {
      val query = assets.filter(_.id === id)

      ZIO.fromDBIO(query.result).provide(self).map(_.headOption).refineOrDie {
        case e: Exception => new RepositoryFailure(e)
      }      
    }

    def getByIds(ids: Set[AssetId]): IO[RepositoryFailure, List[Asset]] = {
      val query = assets.filter(_.id inSet ids)
      
      ZIO.fromDBIO(query.result).provide(self).map(_.toList).refineOrDie {
        case e: Exception => new RepositoryFailure(e)
      }
    }

    def update(id: AssetId, name: String, price: BigDecimal): IO[RepositoryFailure, Unit] = ???
  }

}
