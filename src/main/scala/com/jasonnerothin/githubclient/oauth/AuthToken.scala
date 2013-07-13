package com.jasonnerothin.githubclient.oauth

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/12/13
  * Time: 12:20 PM
  *
  * @param token required token string
  * @param user optional github username
  */
class AuthToken(val token: String, val user: Option[String] = None)
