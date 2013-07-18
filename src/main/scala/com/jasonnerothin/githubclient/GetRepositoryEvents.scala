package com.jasonnerothin.githubclient

import com.jasonnerothin.githubclient.oauth._
import net.liftweb.json.JsonAST.JValue

/** Created by IntelliJ IDEA.
  * User: jason
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
          (implicit settings: OAuthSettings, authCheck: AuthorizationCheck): JValue = {
    null
  }

}
