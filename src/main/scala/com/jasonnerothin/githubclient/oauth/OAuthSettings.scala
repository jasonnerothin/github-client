package com.jasonnerothin.githubclient.oauth

import java.net.URL
import _root_.com.jasonnerothin.githubclient.oauth.AuthScope._

/**
  * Copyright (c) 2013 jasonnerothin.com
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
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