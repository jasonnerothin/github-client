package com.jasonnerothin.githubclient

import com.jasonnerothin.githubclient.oauth._
import net.liftweb.json.JsonAST.JValue

/** Copyright (c) 2013 jasonnerothin.com
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
  * Time: 9:37 PM
  * Provides a mechanism for getting repository events: see
  * http://developer.github.com/v3/activity/events/#list-repository-events for more info...
  */
class GetRepositoryEvents(val repository: String){

  /** An arbitrary Github RESTful http call.
    * @param token must be authenticated
    * @param query optional query params, e.g. ?name=jason&valour=impressive
    * @param params optional JSon payload (e.g. for PUTs)
    * @return a json result
    */
  def call(token: AuthToken, query: Option[Map[String, AnyRef]] = None, params: Option[String])
          (implicit settings: OAuthSettings, authCheck: CheckAuthorization): JValue = {
    null
  }

}
