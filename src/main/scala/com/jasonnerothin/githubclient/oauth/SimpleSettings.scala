package com.jasonnerothin.githubclient.oauth

/**
  * Copyright (c) 2013 jasonnerothin.com
  * User: jason
  * Date: 7/12/13
  * Time: 11:43 PM
  * Provides a 2-or-more-param request class for auth requests
  */
class SimpleSettings(
  val clientId: String,
  val clientSecret: String,
  val githubUser: String,
  val githubPassword: String
) extends OAuthSettings