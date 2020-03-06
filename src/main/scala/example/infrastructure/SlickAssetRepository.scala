package example.infrastructure

import example.domain._
import example.infrastructure.EntityIdMappers._
import example.infrastructure.tables.AssetsTable
import example.interop.slick.dbio._
import example.interop.slick.DatabaseProvider
import slick.jdbc.H2Profile.api._
import zio.{ IO, ZIO }

trait SlickAssetRepository extends AssetRepository with DatabaseProvider { self =>

  val assets = TableQuery[AssetsTable.Assets]

  val assetRepository = new AssetRepository.Service {

    def add(name: String, price: BigDecimal): IO[RepositoryError, AssetId] = {
      val insert = (assets returning assets.map(_.id)) += Asset(None, name, price)
      ZIO.fromDBIO(insert).provide(self).refineOrDie {
        case e: Exception => new RepositoryError(e)
      }
    }

    val getAll: IO[RepositoryError, List[Asset]] = 
      ZIO.fromDBIO(assets.result).provide(self).map(_.toList).refineOrDie {
        case e: Exception => new RepositoryError(e)
      }

    def getByName(name: String): IO[RepositoryError, Option[Asset]] = {
      val query = assets.filter(_.name === name)

      ZIO.fromDBIO(query.result).provide(self).map(_.headOption).refineOrDie {
        case e: Exception => new RepositoryError(e)
      }      
    }

    def getById(id: AssetId): IO[RepositoryError, Option[Asset]] = {
      val query = assets.filter(_.id === id)

      ZIO.fromDBIO(query.result).provide(self).map(_.headOption).refineOrDie {
        case e: Exception => new RepositoryError(e)
      }      
    }

    def getByIds(ids: Set[AssetId]): IO[RepositoryError, List[Asset]] = {
      val query = assets.filter(_.id inSet ids)
      
      ZIO.fromDBIO(query.result).provide(self).map(_.toList).refineOrDie {
        case e: Exception => new RepositoryError(e)
      }
    }

    def update(id: AssetId, name: String, price: BigDecimal): IO[RepositoryError, Unit] = ???
  }

}
