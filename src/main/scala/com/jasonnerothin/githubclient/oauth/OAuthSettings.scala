package com.jasonnerothin.githubclient.oauth

import java.net.URL
import _root_.com.jasonnerothin.githubclient.oauth.AuthScope._

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 12:11 PM
  * Provides a clientId for the OAuth application that you
  * registered for here: https://github.com/settings/applications/new
  *
  * @param redirectUri optional URL to redirect after (successful) authentication. Default's to application default
  * @param loginUrl oauth service provider
  * @param scopes zero or more authorization scopes to request
  * @param state a mechanism to provide an unguessable random string for protection against cross site attacks
  */
abstract class OAuthSettings(
    val redirectUri: Option[URL] = None,
    val loginUrl: Option[URL] = Some(new URL("https://github.com/login/oauth/")),
    val scopes: Array[AuthScope] = Array(AuthScope.default()),
    val state: Option[String] = None
){

  /**
    * @return github application's client id, set up during application registration
    */
  def clientId : String

  /** clientSecret - provided by github at registration of application
    *
    * @return the String therefor, from whichever source
    */
  def clientSecret: String

  /** @return github username
    */
  def githubUser: String

  /** @return github user password
    */
  def githubPassword: String

}