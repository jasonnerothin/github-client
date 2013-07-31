package com.jasonnerothin.githubclient.oauth

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import com.ning.http.client.{AsyncHandler, Response, Request}
import _root_.dispatch.Http
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.{Future, ExecutionContext}
import net.liftweb.json.JsonAST.JValue
import com.jasonnerothin.MyRandom

/**
  * Copyright (c) 2013 jasonnerothin.com
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * Date: 7/16/13
  * Time: 6:44 PM
  */
class CheckAuthorizationSpec extends FunSuite with MockitoSugar {

  val timeoutMs = 1

  implicit val settings:OAuthSettings = mockSettings()

  def systemUnderTest(token: AuthToken = mockAuthToken(), http: Http = mockHttp()):Boolean= {
    CheckAuthorization.authorized(token = token, http = http, timeoutMs = timeoutMs)
  }

  def mockSettings(githubUser: String = randomString(4), clientId: String = randomString(5), githubPassword: String = randomString(7)): OAuthSettings = {
    val settings = mock[OAuthSettings]
    doReturn(githubUser).when(settings).githubUser
    doReturn(clientId).when(settings).clientId
    doReturn(githubPassword).when(settings).githubPassword

    settings
  }

  def mockAuthToken(token: String = randomString(1)): AuthToken = {
    val tok: AuthToken = mock[AuthToken]
    doReturn(token).when(tok).token

    tok
  }

  def mockHttp(result: Boolean = false): Http = {
    val futureResponse: Future[JValue] = mock[Future[JValue]]
    doReturn(result).when(futureResponse).isCompleted

    val http = mock[Http]
    //    doReturn(futureResponse).when(http).apply(isA(classOf[RequestBuilder]))(any[ExecutionContext])
    doReturn(futureResponse).when(http).apply(isA(classOf[(Request, AsyncHandler[Boolean])]))(any[ExecutionContext])

    http
  }

  test("authorized returns false when response doesn't come back quickly enough") {

    val act = systemUnderTest()
    assert(act === false)

  }

  /**
   * This code is called out specifically in the API
   */
  test("authorized returns false when github returns a 404") {

    val statusCode = 404
    statusCodeMeans(statusCode, authorized = false)

  }

  def statusCodeMeans(statusCode: Int, authorized: Boolean) {
    val mockResponse = mock[Response]
    doReturn(statusCode).when(mockResponse).getStatusCode
    val aMockHttp = mockHttp(authorized)
    assert(systemUnderTest(http = aMockHttp) === authorized, "authorized should = %s with code = %s".format(authorized, statusCode))
  }

  test("authorized returns true when response code is in 200s") {

    for (statusCode <- 200 to 299)
      statusCodeMeans(statusCode, authorized = true)

  }

  test("authorized returns false when for various other codes") {

    statusCodeMeans(500, authorized = false)
    statusCodeMeans(501, authorized = false)
    statusCodeMeans(505, authorized = false)
    statusCodeMeans(330, authorized = false)
    statusCodeMeans(110, authorized = false)
    statusCodeMeans(650, authorized = false)
    statusCodeMeans(-123, authorized = false)

  }

  def randomString(len: Int) = MyRandom.randomString(len)

}