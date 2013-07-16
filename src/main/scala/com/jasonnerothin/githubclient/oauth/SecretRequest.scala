package com.jasonnerothin.githubclient.oauth

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 11:43 PM
  * Provides a 2-or-more-param request class for auth requests
  */
class SecretRequest(val clientId: String, val clientSecret: String) extends OAuthRequest