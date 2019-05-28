package example.domain

import scalaz.zio.IO

trait AssetRepository {

  def assetRepository: AssetRepository.Service
  
}

object AssetRepository {
  
  trait Service {

    def add(name: String, price: BigDecimal): IO[Exception, AssetId]

    val getAll: IO[Exception, List[Asset]]
    
    def getById(id: AssetId): IO[Exception, Option[Asset]]

    def getByIds(ids: Set[AssetId]): IO[Exception, List[Asset]]

    def update(id: AssetId, name: String, price: BigDecimal): IO[Exception, Unit]
  }

}

