package example.domain

import zio.IO

trait AssetRepository {

  def assetRepository: AssetRepository.Service
  
}

object AssetRepository {
  
  trait Service {

    def add(name: String, price: BigDecimal): IO[RepositoryError, AssetId]

    val getAll: IO[RepositoryError, List[Asset]]
    
    def getById(id: AssetId): IO[RepositoryError, Option[Asset]]

    def getByIds(ids: Set[AssetId]): IO[RepositoryError, List[Asset]]

    def update(id: AssetId, name: String, price: BigDecimal): IO[RepositoryError, Unit]
  }

}

