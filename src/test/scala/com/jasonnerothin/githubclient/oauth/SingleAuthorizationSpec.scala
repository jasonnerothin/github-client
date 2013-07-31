package com.jasonnerothin.githubclient.oauth

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scala.util.{Try, Random}
import dispatch._, Defaults._
import org.mockito.Matchers._
import com.ning.http.client._
import scala.concurrent.ExecutionContext
import scala.Predef._

import com.jasonnerothin.githubclient.Mock$._
import com.jasonnerothin.githubclient.{FakeHttpProvider, FakeHttpClient, MakeLiftJson}
import scala.Some
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import net.liftweb.json


/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/10/13
 * Time: 10:51 PM
 */
class SingleAuthorizationSpec extends FunSuite with MockitoSugar {

  val rand = new Random
  val defaultId = Math.abs(rand.nextInt())
  val defaultToken = randomString(30)

  implicit val oAuthSettings: OAuthSettings = $oAuthSettings()

  class FakeResponder(jsonAsString: String = authSuccessJson()) extends (Response => String) {
    def apply(response: Response) = jsonAsString
  }

  def systemUnderTest(id: Int = defaultId,
                      tokenStr: String = defaultToken,
                      http: HttpExecutor = mockHttp(authSuccessJson()),
                      makeJson: (Response => json.JValue) = new MakeLiftJson(new FakeResponder())): Option[AuthToken] = {

    (new Object with SingleAuthorization).login(http, makeJson)

  }

  def authSuccessJson(tokenStr: String = defaultToken, id: Int = defaultId): String = { """
    {
      "id":"%s",
      "url": "https://api.github.com/authorizations/%s",
      "app": {
        "name":"Some_Application",
        "url":"https://jasonnerothin.com/",
        "client_id":"41234123412341234123"},
        "token":"%s",
        "note":"simple authorization test",
        "note_url":"feeling a little testy",
        "created_at":"2013-07-22T16:06:20Z",
        "updated_at":"2013-07-22T16:06:20Z",
        "scopes":["public_repo","repo:status"]
    }""".format(id, id, tokenStr)

  }

  def mockHttp(jsonAsString: String = authSuccessJson()): HttpExecutor = {

    val response = mock[Response]
    doReturn("application/json").when(response).getContentType
    doReturn(200).when(response).getStatusCode
    doReturn("OK").when(response).getStatusText
    doReturn(jsonAsString).when(response).getResponseBody
    val buf = ByteBuffer.allocate(jsonAsString.length)
    for (ch <- jsonAsString.toCharArray) buf.put(ch.toByte)
    doReturn(buf).when(response).getResponseBodyAsByteBuffer
    doReturn(buf.array()).when(response).getResponseBodyAsBytes
    doReturn(false).when(response).isRedirected

    val listenableFuture: ListenableFuture[Response] = mock[ListenableFuture[Response]]
    doReturn(response).when(listenableFuture).get
    doReturn(true).when(listenableFuture).isDone
    doReturn(false).when(listenableFuture).isCancelled
    doReturn(response).when(listenableFuture).get(isA(classOf[Int]), isA(classOf[TimeUnit]))

    val futureString = mock[Future[String]]
    doReturn(Some(Try(jsonAsString))).when(futureString).value
    doReturn(true).when(futureString).isCompleted
    doReturn(futureString).when(futureString).map(any())(any[ExecutionContext])

    val provider = new FakeHttpProvider(response = response, listenableFuture = listenableFuture)
    new FakeHttpClient(future = futureString, listenableFutureResponse = listenableFuture, provider = provider)

  }

  test("login returns an actual AuthToken") {

    val result = systemUnderTest()
    assert(result.isDefined)

    val actual = result.get
    actual match {
      case AuthToken(tok, i) => {
        assert(tok === defaultToken)
        assert(i === defaultId)
      }
      case _ => throw new AssertionError("Actual result is not an AuthToken: %s".format(actual.toString))
    }

  }

  test("login returns a token in the AuthToken") (pendingUntilFixed{

    val token = randomString(4)
    val http = mockHttp(jsonAsString = authSuccessJson(tokenStr = token))
    val result = systemUnderTest(http = http, tokenStr = token)

    assert(result.isDefined)
    val actual = result.get.token
    assert(actual === token, "Token '%s' returned from server did not show up in the AuthToken.token '%s'.".format(token, actual))

  })

  /** this isn't technically a requirement of the API, but is a behavior of the trait,
    * so we'll hold it down with a test anyway
    */
  test("login returns an id in the AuthToken")(pendingUntilFixed {

    val testId = Math.abs(rand.nextInt())
    val http = mockHttp(jsonAsString = authSuccessJson(id = testId))
    val result = systemUnderTest(id = testId, http = http)

    assert(result.isDefined)
    assert(result.get.id === testId, "Authorization id '%s' returned from the server isn't in the AuthToken.id field: '%s'.".format(testId, result.get.id))

  })

  test("login returns None when authorization fails")(pendingUntilFixed {

    val http = mockHttp(authSuccessJson())

    val response = mock[Response]
    doReturn(401).when(response).getStatusCode

    val futureResponse = mock[Future[Response]]
    doReturn(true).when(futureResponse).isCompleted
    doReturn(Some(response)).when(futureResponse).value
    doReturn(Some(response)).when(futureResponse).completeOption

    doReturn(futureResponse).when(http).apply(isA(classOf[RequestBuilder]))(any[ExecutionContext])

    val actual = systemUnderTest(http = http)
    assert(actual === None)

  })

}