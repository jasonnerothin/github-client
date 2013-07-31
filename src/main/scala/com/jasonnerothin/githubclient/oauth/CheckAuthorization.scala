package com.jasonnerothin.githubclient.oauth

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json.JsonAST.JValue

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
  * Time: 6:41 PM
  * Provides an implementation of this API: http://developer.github.com/v3/oauth/#check-an-authorization
  */
trait CheckAuthorization {

  /** Whether token is still valid (has not timed out or been revoked)
    * @param token to check
    * @param settings oauth settings to check against
    * @param http http object, provided for test convenience
    * @param timeoutMs amount of time to wait for the server to respond
    * @return if valid
    */
  def authorized(token: AuthToken, settings: OAuthSettings, http: Http = new Http(), timeoutMs: Long = 1250): Boolean = {

    // /applications/:client_id/tokens/:access_token
    def authCheck = url("https://api.github.com/applications/%s/tokens/%s".format(settings.clientId, token.token))
      .secure.as_!(settings.githubUser, settings.githubPassword) <:< Map("Accept:"-> "application/json")

    val result: Future[JValue] = http(authCheck OK as.lift.Json)
    Thread.sleep(timeoutMs)
    result.isCompleted

  }

}

object CheckAuthorization extends Object with CheckAuthorization {
}