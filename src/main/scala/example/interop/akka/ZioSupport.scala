package example.interop

import akka.http.scaladsl.marshalling.Marshaller
import zio.{ DefaultRuntime, ZIO }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpResponse

trait ZioSupport extends DefaultRuntime { self =>

  implicit def zio2Marshaller[A](implicit m1: Marshaller[A, HttpResponse], m2: Marshaller[Throwable, HttpResponse]): Marshaller[ZIO[Any, Throwable, A], HttpResponse] =
    Marshaller { implicit ec => a => self.unsafeRun(a.fold(e => m2(e), a => m1(a))) }

  // implicit def zioMarshaller[A, B](implicit m: Marshaller[A, B]): Marshaller[ZIO[Any, Unit, A], B] =
  //   Marshaller { implicit ec => a => m(self.unsafeRun(a)) }

  implicit def zioRouteMarshaller(zioRoute: ZIO[Any, Throwable, Route]): Route = self.unsafeRun(zioRoute)
}
