package com.jasonnerothin.githubclient.oauth

import java.io.{Reader, InputStream}
import java.util.Properties

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
  * Time: 11:36 PM
  *
  * Provides a 2-or-more-param request class for auth requests
  * @deprecated not sure i want to keep this guy yet... untested, unused
  */
class ClasspathPropertySettings(propertyReader: Reader) extends OAuthSettings{

  /** @return clientSecret - provided by github at registration of application
    */
  def clientSecret:String =  getProperty("clientSecret")

  /** @return github application's client id, set up during application registration
    */
  def clientId: String =  getProperty("clientId")

  /** @return github username
   */
  def githubUser: String = getProperty("github.user")

  /** @return github password
    */
  def githubPassword: String = getProperty("github.password")

  def getProperty(name: String): String = {
    val props = new Properties()
    props.load(propertyReader)
    props.getProperty(name)
  }

}