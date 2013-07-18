package com.jasonnerothin.githubclient

import java.net.URI
import org.springframework.web.bind.annotation.RequestMethod
import com.jasonnerothin.githubclient.oauth.{OAuthSettings, AuthToken, AuthorizationValidation}

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/16/13
 * Time: 7:51 PM
 * Provides a proxy that we can put the HTTP behind and just return JSON for easy usage
 * @param method http action {GET, POST, DELETE, ... }
 * @param baseUri [scheme]://host:port, e.g. https://api.github.com
 * @param requestMapping [scheme-specific-part], e.g. /applications/github_client/
 */
abstract class GithubProxy(val settings: OAuthSettings,
                           val requestMapping: URI,
                           val baseUri: URI = new URI("https://api.github.com/"),
                           val method: RequestMethod = RequestMethod.GET){

  /** An arbitrary RESTful Github call (Json)
    * @param query optional query params, e.g. ?name=jason&valour=impressive
    * @param params optional JSon payload (e.g. for PUTs)
    * @return a json result
   */
  def call(token: AuthToken, query: Option[Map[String, AnyRef]] = None, params: Option[String] = None): String

}
