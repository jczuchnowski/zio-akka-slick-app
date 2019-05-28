package example.interop

import scalaz.zio.{ DefaultRuntime, UIO }
import scalaz.zio.blocking.Blocking
import _root_.slick.util.AsyncExecutor

import scala.concurrent.ExecutionContext

trait AsyncExecutorProvider {
  def asyncExecutorProvider: AsyncExecutorProvider.Service
}

object AsyncExecutorProvider {
  trait Service {
    def asyncExecutor: AsyncExecutor
  }
}

trait ZioSlickExecutorProvider extends AsyncExecutorProvider with Blocking with DefaultRuntime { self =>
  override val asyncExecutorProvider = new AsyncExecutorProvider.Service {
    override val asyncExecutor = new AsyncExecutor() {
      def close(): Unit = ???
      def executionContext: ExecutionContext = self.unsafeRun(self.blocking.blockingExecutor.map(ex => ex.asEC))
    }
  }
}
