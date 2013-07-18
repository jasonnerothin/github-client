package com.jasonnerothin.githubclient.oauth

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scala.util.Random
import dispatch.Http
import org.mockito.Matchers._
import com.ning.http.client.{AsyncHandler, Request}
import scala.concurrent.ExecutionContext
import scala.Predef._
import com.jasonnerothin.MyRandom
import java.util.concurrent.Future

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/10/13
 * Time: 10:51 PM
 */
class SingleAuthorizationSpec extends FunSuite with MockitoSugar{

  val rand = new Random

  def systemUnderTest(id: Int = rand.nextInt(), settings:OAuthSettings = mockSettings(), http:Http = mockHttp(randomString(4))): Option[AuthToken] = {
    new SingleAuthorization{}.login(http)(settings)
  }

  def mockSettings(): OAuthSettings = {
    val settings = mock[OAuthSettings]
    doReturn(randomString(3)).when(settings).clientId
    doReturn(randomString(3)).when(settings).clientSecret
    doReturn(randomString(3)).when(settings).githubPassword
    doReturn(randomString(3)).when(settings).githubUser
    settings
  }

  def mockToken(): AuthToken = {
    val tok = mock[AuthToken]
    doReturn(rand.nextInt()).when(tok).id
    doReturn(randomString(18)).when(tok).token
    tok
  }

  def mockHttp(tokenStr: String = randomString(7)): Http = {
    val http = mock[Http]

    val token = mockToken()
    doReturn(tokenStr).when(token).token

    val futureToken = mock[Future[AuthToken]]
    doReturn(token).when(futureToken).get
    doReturn(futureToken).when(http).apply(isA(classOf[(Request, AsyncHandler[AuthToken])]))(any[ExecutionContext])
    http
  }

  def mockAuthorizationCheck(): AuthorizationCheck = {
    mock[AuthorizationCheck]
  }

  test("login returns a token in the AuthToken")(pending)
  def t1(){

    val tok = randomString(4)
    val http = mockHttp(tokenStr = tok)
    val result = systemUnderTest(http = http)

    assert( result.isDefined )
    val actual = result.get.token
    assert( actual === tok, "Token '%s' returned from server did not show up in the AuthToken.token '%s'.".format(tok, actual))

  }

  /** this isn't technically a requirement of the API, but is a behavior of the trait,
    * so we'll hold it down with a test anyway
    */
  test("login returns an id in the AuthToken")(pending)
  def t2(){
    val settings = mockSettings()
    val testId = rand.nextInt()
    val result = systemUnderTest(settings = settings, id = testId)

    assert( result.isDefined )
    assert( result.get.id === testId, "Authorization id '%s' returned from the server isn't in the AuthToken.id field: '%s'.".format(testId, result.get.id) )

  }

  test("login returns None when authorization fails")(pending)
  def t3(){

    val http = mockHttp()
    doReturn(None).when(http).apply(isA(classOf[(Request, AsyncHandler[AuthToken])]))(any[ExecutionContext])

    val actual = systemUnderTest(http=http)
    assert( actual === true )
  }

  def randomString(len: Int) = MyRandom.randomString(len)

}
