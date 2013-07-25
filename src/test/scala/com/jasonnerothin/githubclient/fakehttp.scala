package com.jasonnerothin.githubclient

import com.ning.http.client._
import dispatch._
import scala.concurrent.duration.Duration
import scala.concurrent.{Awaitable, Future, ExecutionContext, CanAwait}
import scala.util.Try
import scala.Some

import com.jasonnerothin.githubclient.Mock$._

class FakeFuture[T](f: ListenableFuture[T]) extends Object with Future[T]{

  def onComplete[U](func: (Try[T]) => U)(implicit executor: ExecutionContext) {}

  def value = Some(Try(f.get))

  @throws(classOf[Exception])
  def result(atMost: Duration)(implicit permit: CanAwait) = f.get

  @throws(classOf[InterruptedException])
  @throws(classOf[scala.concurrent.TimeoutException])
  def ready(atMost: Duration)(implicit permit: CanAwait) = FakeFuture.this

  def isCompleted = true
}

trait FakeHttpExecutor extends HttpExecutor {
  self: AsyncHttpClient =>
  client
  type HttpPackage[T] = T

}

class FakeHttpClient(val listenableFuture: ListenableFuture[Response],
                     val provider: AsyncHttpProvider = $asyncHttpProvider(),
                     val config: AsyncHttpClientConfig = $asyncHttpClientConfig()) extends AsyncHttpClient(provider, config) with FakeHttpExecutor {

  def client = FakeHttpClient.this

  def listenableFuture2Future[Response](f: ListenableFuture[Response]): Future[Response] = {
    new FakeFuture(f) with Awaitable[Response]
  }

  override def executeRequest[T](request: Request, handler: AsyncHandler[T]) = listenableFuture.asInstanceOf[ListenableFuture[T]]

  override def executeRequest(request: Request) = listenableFuture

  override def apply(builder: RequestBuilder)(implicit executor: ExecutionContext) = {
    listenableFuture2Future(listenableFuture)
  }

  override def apply[T](pair: (Request, AsyncHandler[T]))(implicit executor: ExecutionContext) = listenableFuture2Future(listenableFuture).asInstanceOf[Future[T]]

  override def apply[T](request: Request, handler: AsyncHandler[T])(implicit executor: ExecutionContext) = listenableFuture2Future(listenableFuture).asInstanceOf[Future[T]]

}