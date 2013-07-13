package com.jasonnerothin.githubclient.oauth

import scala.util.parsing.json.JSONObject

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 12:08 PM
  * Provides a mechanism for obtaining an OAuth token from github.
  * See http://developer.github.com/v3/oauth/ for implementation details.
  *
  * We want behavior something like this python example: https://gist.github.com/thruflo/e3fbd47fbb7ee3c626bb
  */
trait Authorizes {

  /** Request an OAuth token.
    *
    * @param request OAuth login information
    * @return Option, containing a successful token
    */
  def login(request: OAuthRequest): Option[AuthToken] = None

  /** Returns the result of the login callback
    *
    * @param token required AuthToken
    * @return the result of the successful redirect
    */
  def callback(token: AuthToken): Option[JSONObject] = None

}
