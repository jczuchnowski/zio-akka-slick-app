package example.domain

import zio.IO

trait AssetRepository {

  def assetRepository: AssetRepository.Service
  
}

object AssetRepository {
  
  trait Service {

    def add(name: String, price: BigDecimal): IO[RepositoryFailure, AssetId]

    val getAll: IO[RepositoryFailure, List[Asset]]
    
    def getById(id: AssetId): IO[RepositoryFailure, Option[Asset]]

    def getByIds(ids: Set[AssetId]): IO[RepositoryFailure, List[Asset]]

    def update(id: AssetId, name: String, price: BigDecimal): IO[RepositoryFailure, Unit]
  }

}

