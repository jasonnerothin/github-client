package com.jasonnerothin.githubclient.oauth

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
  * Time: 11:43 PM
  * Provides a 2-or-more-param request class for auth requests
  */
class SimpleSettings(
  val clientId: String,
  val clientSecret: String,
  val githubUser: String,
  val githubPassword: String
) extends OAuthSettings