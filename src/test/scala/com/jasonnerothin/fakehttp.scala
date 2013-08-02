package com.jasonnerothin

import com.ning.http.client._
import dispatch._
import scala.concurrent.duration.Duration
import scala.concurrent._
import scala.util.Try

import com.jasonnerothin.MockHttp$._
import scala.Some
import scala.reflect.ClassTag
import java.util.concurrent.{Callable, TimeUnit}
import scala.concurrent.Future
import scala.util.Success
import com.ning.http.client.listenable.AbstractListenableFuture
import org.mockito.Mockito._
import scala.Some
import scala.util.Success
import org.scalatest.mock.MockitoSugar.mock
import java.nio.ByteBuffer
import org.mockito.Matchers._
import scala.Some
import scala.util.Success
import scala.Some
import scala.util.Success

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

  override def apply[X](request: Request, handler: AsyncHandler[X])(implicit executor: ExecutionContext): Future[X] = {
    val lfut = client.executeRequest(request, handler)
    val promise = scala.concurrent.Promise[X]()
    promise.complete(scala.util.Try(lfut.get()))
    promise.future
  }

  override def apply(builder: RequestBuilder)(implicit executor: ExecutionContext): Future[Response] = {
    listenableFuture2FutureResponse(listenableFutureResponse)
  }

}

object FakeHttpClient extends Object with MockHttpSugar{

  def apply(responseAsStr: String, statusCode:Int )(implicit context: ExecutionContext): HttpExecutor = {

    val response = mock[Response]
    doReturn("application/json").when(response).getContentType
    doReturn(200).when(response).getStatusCode
    doReturn("OK").when(response).getStatusText
    doReturn(responseAsStr).when(response).getResponseBody
    val buf = ByteBuffer.allocate(responseAsStr.length)
    for (ch <- responseAsStr.toCharArray) buf.put(ch.toByte)
    doReturn(buf).when(response).getResponseBodyAsByteBuffer
    doReturn(buf.array()).when(response).getResponseBodyAsBytes
    doReturn(false).when(response).isRedirected

    val listenableFuture: ListenableFuture[Response] = mock[ListenableFuture[Response]]
    doReturn(response).when(listenableFuture).get
    doReturn(true).when(listenableFuture).isDone
    doReturn(false).when(listenableFuture).isCancelled
    doReturn(response).when(listenableFuture).get(isA(classOf[Int]), isA(classOf[TimeUnit]))

    val futureString = mock[Future[String]]
    doReturn(Some(Try(responseAsStr))).when(futureString).value
    doReturn(true).when(futureString).isCompleted
    doReturn(futureString).when(futureString).map(any())(any[ExecutionContext])

    val provider = new FakeHttpProvider(response = response, listenableFuture = listenableFuture, statusCode = statusCode)
    new FakeHttpClient(future = futureString, listenableFutureResponse = listenableFuture, provider = provider)

  }

}

class FakeHttpProvider(response: Response, listenableFuture: ListenableFuture[Response], statusCode: Int = 200, statusText:String = "OK") extends AsyncHttpProvider {

  def execute[U](request: Request, handler: AsyncHandler[U]): ListenableFuture[U] = {
    val result = handler.onCompleted()
    val status:HttpResponseStatus = new HttpResponseStatus(null, this){
      def getStatusCode = statusCode

      def getStatusText = statusText

      def getProtocolName = "http"

      def getProtocolMajorVersion = 1

      def getProtocolMinorVersion = 1

      def getProtocolText = ""
    }
    handler.onStatusReceived(status)
    val listenableFuture: ListenableFuture[U] = new AbstractListenableFuture[U] {
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
    listenableFuture
  }

  def close(): Unit = {}

  def prepareResponse(status: HttpResponseStatus, headers: HttpResponseHeaders, bodyParts: java.util.List[HttpResponseBodyPart]) = response

}
