package com.jasonnerothin.githubclient

import com.jasonnerothin.githubclient.oauth.{AuthToken, OAuthSettings, CheckAuthorization}
import org.mockito.Matchers
import org.mockito.Mockito._, Matchers._
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import com.jasonnerothin.MyRandom
import com.ning.http.client.{AsyncHandler, Request, AsyncHttpClientConfig, AsyncHttpProvider}
import scala.concurrent.ExecutionContext

trait MockSugar extends MockitoSugar{

  def $authorizationCheck(): CheckAuthorization = {
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

  def randomString(len: Int):String = {
    MyRandom.randomString(len)
  }

  def $asyncHttpProvider(): AsyncHttpProvider = {
    mock[AsyncHttpProvider]
  }

  def $asyncHttpClientConfig(): AsyncHttpClientConfig = {
    mock[AsyncHttpClientConfig]
  }

//  def $executionContext(): ExecutionContext = {
//    mock[ExecutionContext]
//  }

}

object Mock$ extends Object with MockSugar
