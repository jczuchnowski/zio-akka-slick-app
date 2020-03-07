package example.interop

import akka.http.scaladsl.marshalling.{ Marshaller, Marshalling, PredefinedToResponseMarshallers }
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.{ Route, RouteResult }
import example.domain.{ DomainError, RepositoryError, ValidationError }
import zio.{ DefaultRuntime, IO, ZIO }

import scala.concurrent.{ Future, Promise }
import scala.language.implicitConversions
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.RouteResult.Complete

trait ZioSupport extends DefaultRuntime { self =>

  implicit val errorMapper: DomainError => HttpResponse =
    _ match {
      case RepositoryError(cause) => HttpResponse(StatusCodes.InternalServerError)
      case ValidationError(msg)   => HttpResponse(StatusCodes.BadRequest)
    }

  implicit val errorMarshaller: Marshaller[DomainError, HttpResponse] =
    Marshaller { implicit ec => a =>
      PredefinedToResponseMarshallers.fromResponse(a)
    }

  implicit def zioMarshaller[A](implicit m1: Marshaller[A, HttpResponse], m2: Marshaller[DomainError, HttpResponse]): Marshaller[IO[DomainError, A], HttpResponse] =
    Marshaller { implicit ec => a => {
      val r = a.foldM(
        e => IO.fromFuture(implicit ec => m2(e)), 
        a => IO.fromFuture(implicit ec => m1(a))
      )
      
      val p = Promise[List[Marshalling[HttpResponse]]]()
      
      self.unsafeRunAsync(r) { exit => 
        exit.fold(e => p.failure(e.squash), s => p.success(s))
      }
      
      p.future
    }}

  private def fromFunction[A, B](f: A => Future[B]): ZIO[A, Throwable, B] = for {
    a  <- ZIO.fromFunction(f)
    b  <- ZIO.fromFuture(_ => a)
  } yield b

  implicit def zioRoute(z: ZIO[Any, DomainError, Route]): Route = ctx => {
    val p = Promise[RouteResult]()
    
    val f = z.fold(
      e => (ctx: RequestContext) => Future.successful(Complete(errorMapper(e))),
      a => a
    )

    self.unsafeRunAsync(f) { exit => 
      exit.fold(e => p.failure(e.squash), s => p.completeWith(s.apply(ctx)))
    }

    p.future
  }
  
}
