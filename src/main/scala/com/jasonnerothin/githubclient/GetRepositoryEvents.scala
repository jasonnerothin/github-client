package com.jasonnerothin.githubclient

import java.net.URI
import com.jasonnerothin.githubclient.oauth._

/** Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/16/13
  * Time: 9:37 PM
  * Provides a mechanism for getting repository events: see
  * http://developer.github.com/v3/activity/events/#list-repository-events for more info...
  */
class GetRepositoryEvents(val repository: String, override val settings: OAuthSettings)
  extends GithubProxy(settings, requestMapping = new URI("/repos/%s/%s/events".format(settings.githubUser, repository)))
  with AuthorizationValidation {

  /** An arbitrary Github RESTful http call.
    * @param token must be authenticated
    * @param query optional query params, e.g. ?name=jason&valour=impressive
    * @param params optional JSon payload (e.g. for PUTs)
    * @return a json result
    */
  def call(token: AuthToken, query: Option[Map[String, AnyRef]] = None, params: Option[String]): String = {
    validate(token, settings)

//    new JObject(List(JField("foo",JString("bar"))))
    ""
  }

}
