package com.jasonnerothin

import com.jasonnerothin.githubclient.oauth.{AuthToken, OAuthSettings, CheckAuthorization}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import com.ning.http.client.{Response, ListenableFuture, AsyncHttpClientConfig, AsyncHttpProvider}
import com.jasonnerothin.githubclient.api.RepositoryEventTag

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

  def $checkAuthorization(isAuthorized: Boolean = true): CheckAuthorization = {
    mock[CheckAuthorization]
  }

  def $oAuthSettings(): OAuthSettings = {
    val settings = mock[OAuthSettings]
    doReturn(randomString(3)).when(settings).clientId
    doReturn(randomString(3)).when(settings).clientSecret
    doReturn(randomString(3)).when(settings).githubPassword
    doReturn(randomString(3)).when(settings).githubUser
    settings
  }

  def $authToken(): AuthToken = {
    val tok = mock[AuthToken]
    doReturn(new Random().nextInt()).when(tok).id
    doReturn(randomString(18)).when(tok).token
    tok
  }

  def $repositoryEventTag() : RepositoryEventTag = {
    mock[RepositoryEventTag]
  }

}

object MockHttp$ extends Object with MockHttpSugar
