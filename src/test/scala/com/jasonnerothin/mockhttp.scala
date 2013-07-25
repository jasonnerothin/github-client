package com.jasonnerothin.githubclient

import com.jasonnerothin.githubclient.oauth.{AuthToken, OAuthSettings, AuthorizationCheck}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import com.jasonnerothin.MyRandom
import com.ning.http.client.{AsyncHttpClientConfig, AsyncHttpProvider}

trait MockSugar extends MockitoSugar{

  def $authorizationCheck(): AuthorizationCheck = {
    mock[AuthorizationCheck]
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

  def randomString(len: Int):String = {
    MyRandom.randomString(len)
  }

  def $asyncHttpProvider(): AsyncHttpProvider = {
    mock[AsyncHttpProvider]
  }

  def $asyncHttpClientConfig(): AsyncHttpClientConfig = {
    mock[AsyncHttpClientConfig]
  }

}

object Mock$ extends Object with MockSugar
