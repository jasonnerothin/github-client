package com.jasonnerothin.githubclient.oauth

import java.net.URL
import com.jasonnerothin.githubclient.oauth.AuthScope._

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 11:43 PM
  * Provides a 2-or-more-param request class for auth requests
  */
class SecretRequest(clientId: String, val secret: String, redirectUrl: Option[URL] = None, loginURL: Option[URL] = Some(new URL("https://github.com/login/oauth/")), scopes: Array[AuthScope]  = Array(AuthScope.default()), state: Option[String] = None)
  extends OAuthRequest(clientId = clientId){

  /** clientSecret - provided by github at registration of application
    *
    * @return the String therefor, from whichever source
    */
  def clientSecret = {
    secret
  }

}
