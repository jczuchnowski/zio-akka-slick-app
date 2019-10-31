package example.infrastructure

import example.domain.{ AssetId, PortfolioId }
import slick.jdbc.H2Profile.api._

object EntityIdMappers {

  implicit def portfolioIdMapper: BaseColumnType[PortfolioId] = MappedColumnType.base[PortfolioId, Long](
    ent => ent.value,
    value => PortfolioId(value)
  )

  implicit def assetIdMapper: BaseColumnType[AssetId] = MappedColumnType.base[AssetId, Long](
    ent => ent.value,
    value => AssetId(value)
  )

}