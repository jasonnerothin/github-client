package com.jasonnerothin.githubclient.api

import org.scalatest.FunSuite
import com.jasonnerothin.{FakeHttpClient, MockHttpSugar}
import com.jasonnerothin.githubclient.oauth.{CheckAuthorization, AuthToken}

import scala.concurrent.ExecutionContext.Implicits.global
import com.ning.http.client.Request
import org.mockito.Mockito._

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

  test("If the repository is public, simple auth is not used.")(pending) // Since > 5000 requests per hour with simple auth starts costing $

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

  def events200Response(): String = { """
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
                                      """
  }

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



  test("eTag makes it into the request headers")(pendingUntilFixed({

    val getEvents = new GetRepositoryEvents(repositoryName = randomString(4), true)

    val request = mock[Request]

    val fakeExecutor = FakeHttpClient(randomString(3), 200) // TODO pass in mock request or fish the request builder object out of the fake

    getEvents(Some($repositoryEventTag()), fakeExecutor)

    verify(request, times(1)).getHeaders()

  }))

  test("eTag in response headers makes it into RepositoryEventTag")(pending)

  test("Passing a None RepositoryEventTag is just fine.")(pending)

  test("When an eTag is supplied in the request and another is returned in the response, the RepositoryEventTag reflects the update")(pending)

  test("X-Poll-Interval in reponse headers makes it into RepositoryEventTag")(pending)

  test("Link in response headers make it into RepositoryEventTag")(pending)

  test("More than one link in response headers make it into RepositoryEventTag")(pending)

  test("No links in response headers result in empty list of URLs in RepositoryEventTag")(pending)

  test("304 Not Modified response header results in RepositoryEventTag unmodified everywhere but lastChecked.")(pending)

  test("RepositoryEventTag#lastChecked comes back more recent than the corresponding is passed in (304 response).")(pending)

  test("RepositoryEventTag#lastChecked comes back more recent than the corresponding is passed in (200 response).")(pending)

}