package com.jasonnerothin.githubclient.oauth

import _root_.com.jasonnerothin.githubclient.oauth.AuthScope._
import java.io.InputStream
import java.net.URL
import java.util.Properties

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 11:36 PM
  *
  * Provides a 2-or-more-param request class for auth requests
  * @deprecated not sure i want to keep this guy yet... untested, unused
  */
class ClasspathInformedRequest(clientId: String, propertyFile: InputStream, redirectURL: Option[URL] = None, loginURL: Option[URL] = Some(new URL("https://github.com/login/oauth/")), scopes: Array[AuthScope]  = Array(AuthScope.default()), state: Option[String] = None)
    extends OAuthRequest(clientId = clientId){

  /** clientSecret - provided by github at registration of application
    *
    * @return the String therefor, from whichever source
    */
  def clientSecret = {
    val props = new Properties()
    props.load(propertyFile)
    props.getProperty("clientId")
  }

}
