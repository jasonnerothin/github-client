package com.jasonnerothin.githubclient.oauth

import scala.util.parsing.json.JSONObject

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 12:08 PM
  * Provides a mechanism for obtaining an OAuth token from github using
  * the shared-secret single authorization pattern described here:
  * http://developer.github.com/v3/oauth/#get-a-single-authorization
  */
trait SingleAuthorization{

  /** Request an OAuth token.
    *
    * @param request OAuth login information
    * @return Option, containing a successful token
    */
  def login(request: OAuthRequest): Option[AuthToken] = None

  /** Whether token is still valid (has not timed out or been revoked)
    * @param token to check
    * @return if valid
    */
  def stillAuthorized(token: AuthToken): Boolean  = false

}
