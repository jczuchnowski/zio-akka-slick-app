package example.domain

import zio.IO

trait AssetRepository {

  def assetRepository: AssetRepository.Service
  
}

object AssetRepository {
  
  trait Service {

    def add(name: String, price: BigDecimal): IO[RepositoryException, AssetId]

    val getAll: IO[RepositoryException, List[Asset]]
    
    def getById(id: AssetId): IO[RepositoryException, Option[Asset]]

    def getByIds(ids: Set[AssetId]): IO[RepositoryException, List[Asset]]

    def update(id: AssetId, name: String, price: BigDecimal): IO[RepositoryException, Unit]
  }

}

