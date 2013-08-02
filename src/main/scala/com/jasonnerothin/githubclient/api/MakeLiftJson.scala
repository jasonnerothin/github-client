package com.jasonnerothin.githubclient.api

import dispatch._
import net.liftweb.json
import net.liftweb.json.{DefaultFormats, JsonParser}
import com.ning.http.client.Response

/** Copyright (c) 2013 jasonnerothin.com
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
  * Date: 7/23/13
  * Time: 8:07 PM
  * Allows us to mix in a mock responder in place of as.String
  */
class MakeLiftJson(val responder: (Response => String) = as.String)
  extends (Response => json.JValue) {

  implicit val formats = DefaultFormats

  def apply(response: Response): json.JValue = {
    val jv = (responder andThen JsonParser.parse)(response)
    jv
  }

}
