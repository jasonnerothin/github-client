package com.jasonnerothin

import com.ning.http.client._
import dispatch._
import scala.concurrent.duration.Duration
import scala.concurrent._
import scala.util.Try

import com.jasonnerothin.githubclient.Mock$._
import scala.Some
import scala.reflect.ClassTag
import java.util.{concurrent => juc}
import java.util.concurrent.{Callable, Executor, TimeUnit}
import scala.concurrent.Future
import scala.util.Success
import com.ning.http.client.listenable.AbstractListenableFuture

class FakeFuture[+T](future: ListenableFuture[T]) extends Object with Future[T] {

  implicit def listenableFuture2Future(listenableFuture: ListenableFuture[_]): Future[T] = {
    new FakeFuture(listenableFuture.asInstanceOf[ListenableFuture[T]]) with Awaitable[T]
  }

  override def onComplete[U](func: (Try[T]) => U)(implicit executor: ExecutionContext): Unit = {
    listenableFuture2Future(future).value
  }

  override def onSuccess[U](pf: PartialFunction[T, U])(implicit executor: ExecutionContext): Unit = {
    Success(future.get)
  }

  override def foreach[U](f: (T) => U)(implicit executor: ExecutionContext): Unit = onComplete {
    case Success(s) => f(s)
    case _ =>
  }(executor)

  override def value: Option[Try[T]] = {
    val v = future.get match {
      case c: Try[_] => Some(c.asInstanceOf[Try[T]])
      case d: AnyRef => Some(Try(d).asInstanceOf[Try[T]])
    }
    // Some(Try(future.get))
    // val v = listenableFuture2FutureResponse(future).value
    v
  }

  def future[S](): Future[S] = {
    listenableFuture2Future(future).asInstanceOf[Future[S]]
  }

  override def map[S](f: (T) => S)(implicit executor: ExecutionContext): Future[S] = {
    future()
  }

  override def transform[S](s: T => S, f: Throwable => Throwable)(implicit executor: ExecutionContext): Future[S] = {
    future()
  }

  override def flatMap[S](f: T => Future[S])(implicit executor: ExecutionContext): Future[S] = {
    future()
  }

  override def collect[S](pf: PartialFunction[T, S])(implicit executor: ExecutionContext): Future[S] = {
    future()
  }

  override def recover[U >: T](pf: PartialFunction[Throwable, U])(implicit executor: ExecutionContext): Future[U] = {
    future()
  }

  override def recoverWith[U >: T](pf: PartialFunction[Throwable, Future[U]])(implicit executor: ExecutionContext): Future[U] = {
    future()
  }

  override def filter(pred: T => Boolean)(implicit executor: ExecutionContext): Future[T] = {
    future()
  }

  @throws(classOf[Exception])
  override def result(atMost: Duration)(implicit permit: CanAwait) = {
    future.get
  }

  @throws(classOf[InterruptedException])
  @throws(classOf[scala.concurrent.TimeoutException])
  override def ready(atMost: Duration)(implicit permit: CanAwait) = {
    FakeFuture.this
  }

  override def isCompleted = {
    true
  }

  override def mapTo[S](implicit tag: ClassTag[S]): Future[S] = {
    future()
  }

}

class FakeHttpClient[U](val future: Future[U],
                        val listenableFutureResponse: ListenableFuture[Response],
                        val provider: AsyncHttpProvider,
                        val config: AsyncHttpClientConfig = $asyncHttpClientConfig())
                       (implicit executor: ExecutionContext)
  extends AsyncHttpClient(provider, config)
  with HttpExecutor {

  def client = FakeHttpClient.this

  def listenableFuture2FutureResponse(f: ListenableFuture[Response]): Future[Response] = new FakeFuture[Response](f) with Awaitable[Response]

  override def executeRequest(request: Request): ListenableFuture[Response] = {
    super.executeRequest(request)
    //    listenableFuture.asInstanceOf[ListenableFuture[Response]]
  }

  override def executeRequest[X](request: Request, handler: AsyncHandler[X]): ListenableFuture[X] = {
    super.executeRequest[X](request, handler)
    //    listenableFuture.asInstanceOf[ListenableFuture[X]]
  }


  override def apply[X](request: Request, handler: AsyncHandler[X])(implicit executor: ExecutionContext): Future[X] = {
//    super.apply[X](request, handler)
    val lfut = client.executeRequest(request, handler)
    val promise = scala.concurrent.Promise[X]()
    promise.complete(scala.util.Try(lfut.get()))
//    lfut.addListener(
//      () => promise.complete(util.Try(lfut.get())),
//      new juc.Executor {
//        def execute(runnable: Runnable) {
//          executor.execute(runnable)
//        }
//      }
//    )
    promise.future
  }

  override def apply(builder: RequestBuilder)(implicit executor: ExecutionContext): Future[Response] = {
    //    apply(builder.build() -> new FunctionHandler(identity))
    listenableFuture2FutureResponse(listenableFutureResponse)
  }

  override def apply[X](pair: (Request, AsyncHandler[X]))(implicit executor: ExecutionContext): Future[X] = {
    super.apply(pair)(executor)
    //    future.asInstanceOf[Future[X]]
  }
}

class FakeHttpProvider(response: Response, listenableFuture: ListenableFuture[Response]) extends AsyncHttpProvider{

  def execute[U](request: Request, handler: AsyncHandler[U]):ListenableFuture[U] = {
    val result = handler.onCompleted()
    val fut: ListenableFuture[U] = new ListenableFuture[U] {
      def isCancelled = false

      def get(timeout: Long, unit: TimeUnit):U = result

      def get():U = result

      def cancel(mayInterruptIfRunning: Boolean):Boolean = true

      def done(callable: Callable[_]):Unit = {}

      def isDone:Boolean = true

      def touch():Unit = {}

      def getAndSetWriteBody(writeBody: Boolean):Boolean = writeBody

      def content(v: U) {}

      def addListener(listener: Runnable, exec: Executor):ListenableFuture[U] = this

      def getAndSetWriteHeaders(writeHeader: Boolean):Boolean = false

      def abort(t: Throwable):Unit= {}
    }
    val fut2: ListenableFuture[U] = new AbstractListenableFuture[U] {
      def isCancelled = false

      def get(timeout: Long, unit: TimeUnit) = result

      def get() = result

      def cancel(mayInterruptIfRunning: Boolean) = true

      def done(callable: Callable[_]) {}

      def isDone = true

      def touch() {}

      def getAndSetWriteBody(writeBody: Boolean) = true

      def content(v: U) {}

      def getAndSetWriteHeaders(writeHeader: Boolean) = true

      def abort(t: Throwable) {}
    }
    fut2
  }

  def close():Unit = {}

  def prepareResponse(status: HttpResponseStatus, headers: HttpResponseHeaders, bodyParts: java.util.List[HttpResponseBodyPart]) = response
}
