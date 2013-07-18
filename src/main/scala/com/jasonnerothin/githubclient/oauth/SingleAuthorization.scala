package com.jasonnerothin.githubclient.oauth

import dispatch.Http

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 12:08 PM
  * Provides a mechanism for obtaining an OAuth token from github using
  * the shared-secret single authorization pattern described here:
  * http://developer.github.com/v3/oauth/#create-a-new-authorization
  */
trait SingleAuthorization {

  /** Request an OAuth token.
    *
    * @param settings OAuth login information
    * @return Option, containing a successful token
    */
  def login(http:Http)(implicit settings: OAuthSettings): Option[AuthToken] = None

}

object SingleAuthorization extends Object with SingleAuthorization{}
