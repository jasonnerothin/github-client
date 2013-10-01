package com.jasonnerothin

import com.jasonnerothin.githubclient.oauth.{AuthToken, OAuthSettings, CheckAuthorization}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import com.ning.http.client._
import com.jasonnerothin.githubclient.api.RepositoryEventTag
import org.mockito.Matchers
import org.mockito.Matchers._
import dispatch.Http
import scala.concurrent.{ExecutionContext, Future}
import net.liftweb.json.JsonAST.JValue
import com.jasonnerothin.githubclient.api.RepositoryEventTag
import com.jasonnerothin.githubclient.oauth.AuthToken
import com.jasonnerothin.githubclient.api.RepositoryEventTag
import com.jasonnerothin.githubclient.oauth.AuthToken

trait MockHttpSugar extends MockitoSugar{

  def randomString(len: Int):String = {
    MyRandom.randomString(len)
  }

  def $asyncHttpProvider(): AsyncHttpProvider = {
    mock[AsyncHttpProvider]
  }

  def $asyncHttpClientConfig(): AsyncHttpClientConfig = {
    mock[AsyncHttpClientConfig]
  }

  def $checkAuthorization(isAuthorized: Boolean = true, http: Http = mock[Http], timeout: Long = 1L): CheckAuthorization = {
    val checkAuth = mock[CheckAuthorization]
    doReturn(isAuthorized).when(checkAuth).authorized(isA(classOf[AuthToken]), Matchers.eq(http), Matchers.eq(timeout))(isA(classOf[OAuthSettings]))
//    doReturn(isAuthorized).when(checkAuth).authorized(isA(classOf[AuthToken]), Matchers.isNull(classOf[Http]), Matchers.isNull(classOf[Long]))(isA(classOf[OAuthSettings]))
//    doReturn(isAuthorized).when(checkAuth).authorized(isA(classOf[AuthToken]), Matchers.isNull(classOf[Http]), Matchers.eq(timeout))(isA(classOf[OAuthSettings]))
//    doReturn(isAuthorized).when(checkAuth).authorized(isA(classOf[AuthToken]), Matchers.eq(http), Matchers.isNull(classOf[Long]))(isA(classOf[OAuthSettings]))
    checkAuth
  }

  def $oAuthSettings(): OAuthSettings = {
    val settings = mock[OAuthSettings]
    doReturn(randomString(3)).when(settings).clientId
    doReturn(randomString(3)).when(settings).clientSecret
    doReturn(randomString(3)).when(settings).githubPassword
    doReturn(randomString(3)).when(settings).githubUser
    settings
  }

  def $authToken(tokStr:String = randomString(18)): AuthToken = {
    val tok = mock[AuthToken]
    doReturn(new Random().nextInt()).when(tok).id
    doReturn(tokStr).when(tok).token
    tok
  }

  def $repositoryEventTag() : RepositoryEventTag = {
    mock[RepositoryEventTag]
  }

  def $http(isCompleted:Boolean = true) : Http = {
    val http = mock[Http]
    val futureResponse: Future[JValue] = mock[Future[JValue]]
    doReturn(isCompleted).when(futureResponse).isCompleted
    doReturn(futureResponse).when(http).apply(isA(classOf[(Request, AsyncHandler[Boolean])]))(any[ExecutionContext])
    http
  }

}

object MockHttp$ extends Object with MockHttpSugar
