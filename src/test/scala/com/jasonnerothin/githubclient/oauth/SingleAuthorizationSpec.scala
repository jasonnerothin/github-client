package com.jasonnerothin.githubclient.oauth

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scala.util.Random
import java.net.URL
import _root_.com.jasonnerothin.githubclient.oauth.AuthScope._
import scala.util.parsing.json.JSONObject

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/10/13
 * Time: 10:51 PM
 */
class SingleAuthorizationSpec extends FunSuite with MockitoSugar{

  val auth = new SingleAuthorization {}
  val rand = new Random
  val testClientId = rand.nextString(4)
  val testClientSecret = rand.nextString(6)
  val testLoginUrl = Some(mock[URL])
  val testScopes = mock[Array[AuthScope]]
  val testState = None

  def mockRequest() = {
    val request = mock[OAuthSettings]
    when(request.clientId).thenReturn(testClientId)
    when(request.clientSecret).thenReturn(testClientSecret)
    when(request.loginUrl).thenReturn(testLoginUrl)
    when(request.redirectUri).thenReturn(None)
    when(request.scopes).thenReturn(testScopes)
    when(request.state).thenReturn(testState)
    request
  }

  def mockGithubServer() = {

  }

  test("login returns a token on happy path"){

    val result = systemUnderTest()

    assert( !result.isEmpty )

    val token = result.get.token
    assert( token != None )
    assert( !token.isEmpty )

  }

  def systemUnderTest(): Option[AuthToken] = {
    val request = mockRequest()
    auth.login(request)
  }

  /** this isn't technically a requirement of the API, but is a behavior of the trait,
    * so we'll hold it down with a test anyway
    */
  test("login returns a user in the AuthToken"){
    val result = systemUnderTest().get
    val user = result.user.get
    assert( user != None )
    assert( !user.isEmpty )
  }

  def mockBadMessage(message: String): JSONObject = {
    val badCredentials = mock[JSONObject]
    val badMap = mock[Map[String, AnyRef]]
    when(badCredentials.obj).thenReturn(badMap)
    doReturn(message).when(badMap get "message")
    badCredentials
  }

  def mockBadCredentials(){
    mockBadMessage("Bad credentials")
  }

  def mockBadClientInfo(){
    mockBadMessage("Invalid OAuth application client_id or secret.")
  }

  test("login returns None when we use a bad client_id") {
    val badClientInfo = mockBadClientInfo()

  }

  test("login returns None when we use a bad client_secret"){
    val badClientInfo = mockBadClientInfo()


  }

  test("login returns None when we use a bad username"){
    val badCredentials = mockBadCredentials()

  }

  test("login returns None when we use a bad password"){
    val badCredentials = mockBadCredentials()
  }

}
