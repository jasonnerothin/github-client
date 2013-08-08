package com.jasonnerothin.githubclient.oauth

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import dispatch._, Defaults._
import com.ning.http.client._
import scala.Predef._

import com.jasonnerothin.MockHttp$._
import net.liftweb.json
import com.jasonnerothin.{FakeHttpProvider, FakeHttpClient}
import com.jasonnerothin.githubclient.api.MakeLiftJson

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
  * Date: 7/10/13
  * Time: 10:51 PM
  */
class SingleAuthorizationSpec extends FunSuite with MockitoSugar {

  val rand = new Random
  val defaultId = Math.abs(rand.nextInt())
  val defaultToken = randomString(30)

  implicit val oAuthSettings: OAuthSettings = $oAuthSettings()

  class FakeResponder(jsonAsString: String = authSuccessJson(tokenStr=defaultToken, id=defaultId)) extends (Response => String) {
    def apply(response: Response) = jsonAsString
  }

  def systemUnderTest(id: Int = defaultId,
                      myTokenStr: String = defaultToken,
                      http: HttpExecutor = mockHttp(authSuccessJson(tokenStr = defaultToken, id=defaultId)),
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

  def mockHttp(jsonAsString: String = authSuccessJson(), statusCode:Int = 200): HttpExecutor = {
    FakeHttpClient(jsonAsString, statusCode, httpProvider = None)
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

  test("login returns a token in the AuthToken") {

    val token = randomString(4)
    val result = parameterizedSUT(token, defaultId)

    assert(result.isDefined)
    val actual = result.get.token
    assert(actual === token, "Token string '%s' (returned from the server) did not end up in AuthToken.token '%s'.".format(token, actual))

  }

  def parameterizedSUT(token: String, testId: Int): Option[AuthToken] = {
    val json = authSuccessJson(tokenStr = token, id = testId)
    val http = mockHttp(jsonAsString = json)
    val result = systemUnderTest(http = http, makeJson = new MakeLiftJson(new FakeResponder(jsonAsString = json)))
    result
  }

  /** this isn't technically a requirement of the API, but is a behavior of the trait,
    * so we'll hold it down with a test anyway
    */
  test("login returns an id in the AuthToken"){

    val testId = Math.abs(rand.nextInt())
    val result = parameterizedSUT(defaultToken, testId)

    assert(result.isDefined)
    assert(result.get.id === testId, "Authorization id '%s' returned from the server isn't in the AuthToken.id field: '%s'.".format(testId, result.get.id))

  }

  test("login returns None when authorization fails"){

    val http = mockHttp(authSuccessJson(), statusCode = 401)

    val actual = systemUnderTest(http = http)
    assert(actual === None)

  }

}