package example.application

import example.domain.{ Asset, AssetId, AssetRepository }
import example.domain.{ PortfolioAssetRepository, PortfolioAsset, PortfolioId, RepositoryError }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito
import zio.{ Runtime, IO }
import zio.internal.Platform

class MockedSpec extends AnyFlatSpec with Matchers with Runtime[Unit] with MockitoSugar {
  
  override val environment: Unit = Runtime.default.environment

  override val platform: Platform = Runtime.default.platform

  val mockedAssetRepository: AssetRepository = new AssetRepository() {
    val assetRepository: AssetRepository.Service = mock[AssetRepository.Service]
  }

  val mockedPortfolioAssetRepository: PortfolioAssetRepository = new PortfolioAssetRepository() {
    val portfolioAssetRepository: PortfolioAssetRepository.Service = mock[PortfolioAssetRepository.Service]
  }

  "The Hello object" should "say hello" in {
    Mockito.when(mockedAssetRepository.assetRepository.getAll).thenReturn(IO {
      List.empty
    } refineOrDie {
      case e: Exception => RepositoryError(e)
    })
    val result = this.unsafeRun(ApplicationService.getAssets.provide(mockedAssetRepository))
    result shouldEqual(List.empty)
  }
  
  "ApplicationService.getPortfolio" should "return a portfolio" in {
    val portfolioId = PortfolioId(1)

    Mockito.when(mockedPortfolioAssetRepository.portfolioAssetRepository.getByPortfolioId(portfolioId)).thenReturn(IO {
      List(
        PortfolioAsset(portfolioId, AssetId(1), BigDecimal(1)), 
        PortfolioAsset(portfolioId, AssetId(2), BigDecimal(1)))
    } refineOrDie {
      case e: Exception => RepositoryError(e)
    })

    Mockito.when(mockedAssetRepository.assetRepository.getByIds(Set(AssetId(1), AssetId(2)))).thenReturn(IO {
      List(
        Asset(Some(AssetId(1)), "PLN", BigDecimal(1)), 
        Asset(Some(AssetId(2)), "USD", BigDecimal(1)))
    } refineOrDie {
      case e: Exception => RepositoryError(e)
    })

  }

}
