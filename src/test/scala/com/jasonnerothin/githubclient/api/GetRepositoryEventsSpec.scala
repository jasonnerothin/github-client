package com.jasonnerothin.githubclient.api

import org.scalatest.FunSuite
import com.jasonnerothin._
import com.jasonnerothin.githubclient.oauth.CheckAuthorization

import scala.concurrent.ExecutionContext.Implicits.global
import com.ning.http.client._
import org.mockito.Mockito._
import org.apache.commons.httpclient._
import java.util.concurrent.Executors
import org.mockito.Matchers
import org.mockito.Matchers._
import org.apache.commons.httpclient.params.HttpClientParams
import com.ning.http.client.providers.apache.TestableApacheAsyncHttpProvider.HttpMethodFactory
import scala.util.Random
import org.joda.time.DateTime
import com.ning.http.client.providers.apache.TestableApacheAsyncHttpProvider
import java.util.Arrays.asList
import com.jasonnerothin.githubclient.oauth.AuthToken
import scala.Some
import java.io.ByteArrayInputStream

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
 * Date: 7/18/13
 * Time: 1:33 PM
 */
class GetRepositoryEventsSpec extends FunSuite with MockHttpSugar {

  implicit val oAuthSettings = $oAuthSettings()

  test("You can't get repository events if you aren't authorized.") {

    ensureNotAuthorized(Some($authToken()), $checkAuthorization(isAuthorized = false))

  }

  def ensureNotAuthorized(tok: Option[AuthToken], auth: CheckAuthorization) {

    val getEvents = new GetRepositoryEvents("repoName")

    val caught = intercept[IllegalArgumentException] {
      getEvents(Some($repositoryEventTag()))($oAuthSettings(), tok, auth)
    }

    val pattern = ".*Not authorized.*"
    val msg = caught.getMessage

    assert(msg.matches(pattern), "Unexpected error message: [%s]".format(msg))
  }

  test("You can't get repository events from a private repository if you don't pass an auth token.") {

    ensureNotAuthorized(None, $checkAuthorization(isAuthorized = true))

  }

  test("If the repository is public, you don't need authorization to check for new events.") {

    val getEvents = new GetRepositoryEvents("someRepository", repositoryIsPublic = true)

    getEvents(Some($repositoryEventTag()))($oAuthSettings(), Some($authToken()), $checkAuthorization(isAuthorized = false))

  }

  // Since > 5000 requests per hour with simple auth starts costing $
  test("If the repository is public, simple auth is not used.")(pending)
  def foo{
    val getEvents = new GetRepositoryEvents("blahBlahBlah", repositoryIsPublic = true)

    getEvents(Some($repositoryEventTag()))($oAuthSettings(), Some($authToken()), $checkAuthorization(isAuthorized = false))
    fail()
  }

  val events200Headers = """
    |#> HTTP/1.1 200 OK
    |#> Server: GitHub.com
    |#> Date: Wed, 31 Jul 2013 19:10:15 GMT
    |#> Content-Type: application/json; charset=utf-8
    |#> Status: 200 OK
    |#> X-RateLimit-Limit: 60
    |#> X-RateLimit-Remaining: 59
    |#> X-RateLimit-Reset: 1375301415
    |#> X-Poll-Interval: 60
    |#> X-GitHub-Media-Type: github.beta
    |#> Link: <https://api.github.com/repositories/10984736/events?page=1>; rel="prev"
    |#> X-Content-Type-Options: nosniff
    |#> Content-Length: 5
    |#> Access-Control-Allow-Credentials: true
    |#> Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
    |#> Access-Control-Allow-Origin: *
    |#> Vary: Accept-Encoding
  """.stripMargin('>')

  val events200Response: String = """
       [
        {
          "id": "1793559780",
          "type": "PushEvent",
          "actor": {
            "id": 449078,
            "login": "jasonnerothin",
            "gravatar_id": "84d1bd6afe9ff079d54783756a90b07c",
            "url": "https://api.github.com/users/jasonnerothin",
            "avatar_url": "https://secure.gravatar.com/avatar/84d1bd6afe9ff079d54783756a90b07c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
          },
          "repo": {
            "id": 10984736,
            "name": "jasonnerothin/sillytestrepo",
            "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo"
          },
          "payload": {
            "push_id": 208493932,
            "size": 1,
            "distinct_size": 1,
            "ref": "refs/heads/master",
            "head": "df67aa9a04471de24c3a74d049b8ac82d415c2b0",
            "before": "5e63c95aa2fd570aa2dbf6d2383d6f5446c44d3f",
            "commits": [
              {
                "sha": "df67aa9a04471de24c3a74d049b8ac82d415c2b0",
                "author": {
                  "email": "jason.nerothin@gmail.com",
                  "name": "Jason Nerothin"
                },
                "message": "adding an ignore file",
                "distinct": true,
                "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo/commits/df67aa9a04471de24c3a74d049b8ac82d415c2b0"
              }
            ]
          },
          "public": true,
          "created_at": "2013-07-31T20:00:32Z"
        },
        {
          "id": "1766928187",
          "type": "CreateEvent",
          "actor": {
            "id": 449078,
            "login": "jasonnerothin",
            "gravatar_id": "84d1bd6afe9ff079d54783756a90b07c",
            "url": "https://api.github.com/users/jasonnerothin",
            "avatar_url": "https://secure.gravatar.com/avatar/84d1bd6afe9ff079d54783756a90b07c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
          },
          "repo": {
            "id": 10984736,
            "name": "jasonnerothin/sillytestrepo",
            "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo"
          },
          "payload": {
            "ref": null,
            "ref_type": "repository",
            "master_branch": "master",
            "description": ""
          },
          "public": true,
          "created_at": "2013-06-27T02:08:22Z"
        }
       ]
""".stripMargin

  val events200Responder = new MakeLiftJson(new FakeResponder(events200Response))

  val events304Headers = """
    |#>  HTTP/1.1 304 Not Modified
    |#>  Server: GitHub.com
    |#>  Date: Thu, 01 Aug 2013 02:17:16 GMT
    |#>  Status: 304 Not Modified
    |#>  X-RateLimit-Limit: 5000
    |#>  X-RateLimit-Remaining: 4999
    |#>  X-RateLimit-Reset: 1375326908
    |#>  X-Content-Type-Options: nosniff
    |#>  Access-Control-Allow-Credentials: true
    |#>  Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
    |#>  Access-Control-Allow-Origin: *
    |#>  Vary: Accept-Encoding
  """.stripMargin('>')

  test("ETag makes it into the request headers.")(pending) // works when authentication is turned off - fix auth mocking!
  def wibble():Unit = {

    val testEtag = randomString(30)

    val testEventTag: RepositoryEventTag = $repositoryEventTag()
    doReturn(testEtag).when(testEventTag).eTag

    val testHeaderName = "ETag"
    val testHeaderValue = testEtag

    verifyRequestHeader(testHeaderName, testHeaderValue, Some(testEventTag))

  }

  def verifyRequestHeader(testHeaderName: String, testHeaderValue: String, eventTag: Option[RepositoryEventTag] = None) {

    val config: AsyncHttpClientConfig = createMockAsyncHttpConfig
    val client: HttpClient = createMockHttpClient
    val method: HttpMethodBase = mock[HttpMethodBase]
    val methodFactory: TestableApacheAsyncHttpProvider.HttpMethodFactory = createMockMethodFactory(method)
    val fakeExecutor = FakeHttpClient(randomString(3), 200, Some(new TestableHttpProvider(config, client, methodFactory = methodFactory)))

    testInstance()(eventTag, fakeExecutor, events200Responder)($oAuthSettings(), Some($authToken()))

    verify(method).setRequestHeader(testHeaderName, testHeaderValue)

  }

  def createMockMethodFactory(method:HttpMethodBase = mock[HttpMethodBase]): TestableApacheAsyncHttpProvider.HttpMethodFactory = {
    val methodFactory: HttpMethodFactory = mock[HttpMethodFactory]
    doReturn(method).when(methodFactory).createMethod(anyString(), isA(classOf[Request]))
    methodFactory
  }

  def createMockHttpClient: HttpClient = {
    val client: HttpClient = mock[HttpClient]
    doReturn(200).when(client).executeMethod(Matchers.isA(classOf[HttpMethodBase]))
    val params: HttpClientParams = mock[HttpClientParams]
    doReturn(params).when(client).getParams
    val httpState = mock[HttpState]
    doReturn(httpState).when(client).getState
    client
  }

  def createMockAsyncHttpConfig: AsyncHttpClientConfig = {
    val config = mock[AsyncHttpClientConfig]
    doReturn(5).when(config).getMaxTotalConnections
    doReturn(1000000).when(config).getRequestTimeoutInMs
    doReturn(Executors.newFixedThreadPool(2)).when(config).executorService()
    doReturn(Executors.newScheduledThreadPool(1)).when(config).reaper()
    config
  }

  test("responseType is set to application/json in request headers."){

    val accept = "Accept"
    val appJson = "application/json"

    verifyRequestHeader(accept, appJson, Some($repositoryEventTag()))

  }

  val eTag200Headers =
    """
      |#> HTTP/1.1 200 OK
      |#> Server: GitHub.com
      |#> Date: Wed, 31 Jul 2013 17:29:09 GMT
      |#> Content-Type: application/json; charset=utf-8
      |#> Status: 200 OK
      |#> X-RateLimit-Limit: 5000
      |#> X-RateLimit-Remaining: 4999
      |#> X-RateLimit-Reset: 1375295349
      |#> Cache-Control: private, max-age=60, s-maxage=60
      |#> Last-Modified: Thu, 27 Jun 2013 02:08:22 GMT
      |#> ETag: "2632cec2cfe953d2b6effa038266b7e9" !!!!!!!! CHANGED !!!!!!!!!
      |#> X-Poll-Interval: 60
      |#> Vary: Accept, Authorization, Cookie
      |#> X-GitHub-Media-Type: github.beta
      |#> Link: <https://api.github.com/repositories/10984736/events?page=2>; rel="next" !!!! CAPTURE ME !!!
      |#> X-Content-Type-Options: nosniff
      |#> Content-Length: 787
      |#> Access-Control-Allow-Credentials: true
      |#> Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
      |#> Access-Control-Allow-Origin: *
      |#> Vary: Accept-Encoding
    """.stripMargin('>')

  var response200Stream = new ByteArrayInputStream(events200Response.getBytes("UTF-8"))

  test("ETag in response headers makes it into RepositoryEventTag.") (pending)
  // need to get the auth mocking to work (otherwise this test actually works!)
  def bar(): Unit ={

    val testETagValue = randomString(30)
    val response:Response = mock[Response]
    val headers:FluentCaseInsensitiveStringsMap = mock[FluentCaseInsensitiveStringsMap]

    val eTagName = "ETag"
    doReturn(1).when(headers).size()
    doReturn(false).when(headers).isEmpty
    doReturn(true).when(headers).containsKey(eTagName)
    doReturn(true).when(headers).containsValue(testETagValue)
    doReturn(asList(testETagValue)).when(headers).get(Matchers.eq(eTagName))

    doReturn(testETagValue).when(response).getHeader(Matchers.eq(eTagName))
    doReturn(asList(testETagValue)).when(response).getHeaders(Matchers.eq(eTagName))
    doReturn(headers).when(response).getHeaders

    val inputTag = $repositoryEventTag()
    doReturn(randomString(4)).when(inputTag).eTag
    doReturn(Math.abs(Random.nextInt())).when(inputTag).xPollIntervalInSecs
    doReturn(List()).when(inputTag).followLinks
    doReturn(new DateTime()).when(inputTag).lastChecked

    var method = mock[HttpMethodBase]
    var header = mock[Header]
    doReturn(eTagName).when(header).getName
    doReturn(testETagValue).when(header).getValue
    val eTagHeaderArray = Array[Header](header)
    doReturn(eTagHeaderArray).when(method).getResponseHeaders
    doReturn(eTagHeaderArray).when(method).getResponseHeaders(Matchers.eq(eTagName))
    doReturn(Array[Header]()).when(method).getResponseFooters
    doReturn("GET").when(method).getName
    doReturn(response200Stream).when(method).getResponseBodyAsStream

    val methodFactory: TestableApacheAsyncHttpProvider.HttpMethodFactory = createMockMethodFactory(method)
//    val httpProvider:AsyncHttpProvider = mock[AsyncHttpProvider] //new TestableHttpProvider(createMockAsyncHttpConfig, createMockHttpClient, methodFactory = methodFactory)
    val httpProvider:AsyncHttpProvider = new TestableHttpProvider(createMockAsyncHttpConfig, createMockHttpClient, methodFactory = methodFactory)
    val fakeExecutor = FakeHttpClient(events200Response, 200, httpProvider = Some(httpProvider))

    val someInput = Some(inputTag)
    val settings = $oAuthSettings()
    val someToken = Some($authToken())
    val testInst = testInstance()
    val checkAuth = $checkAuthorization(isAuthorized = true, $http(), Math.abs(new Random().nextInt(23)))
    val actual = testInst(someInput, fakeExecutor, events200Responder)(settings, someToken, checkAuth)

    val returnTag = actual._1

    assert( returnTag.eTag === testETagValue )

  }

  def testInstance(): GetRepositoryEvents = {
    new GetRepositoryEvents(repositoryName = randomString(4))
  }

  test("Passing a None RepositoryEventTag is just fine.")(pending)

  test("When an ETag is supplied in the request and another is returned in the response, the RepositoryEventTag reflects the update.")(pending)

  test("X-Poll-Interval in response headers makes it into RepositoryEventTag.")(pending)

  test("Links in response headers make it into RepositoryEventTag.")(pending)

  test("More than one link in response headers make it into RepositoryEventTag.")(pending)

  test("No links in response headers result in empty list of URLs in RepositoryEventTag.")(pending)

  test("304 Not Modified response header results in RepositoryEventTag unmodified everywhere but lastChecked.")(pending)

  test("RepositoryEventTag#lastChecked comes back more recent than the corresponding value that was passed in (304 response).")(pending)

  test("RepositoryEventTag#lastChecked comes back more recent than the corresponding value that was passed in (200 response).")(pending)

}