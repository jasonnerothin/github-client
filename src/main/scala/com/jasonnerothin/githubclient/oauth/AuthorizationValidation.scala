package com.jasonnerothin.githubclient.oauth

import scala.tools.util.PathResolver.Defaults
import dispatch._, Defaults._
/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/16/13
  * Time: 9:58 PM
  * Provides...
  */
trait AuthorizationValidation extends AuthorizationCheck{

  /** Check to see if the token is still valid
    * @param token to check
    */
  def validate(token: AuthToken, settings: OAuthSettings): Unit = {
    if( !authorized(token, settings) )
      throw new IllegalStateException("No longer authorized to list events.")
  }
}
