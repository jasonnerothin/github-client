package com.jasonnerothin.githubclient.oauth

import dispatch._

import net.liftweb.json._
import scala.concurrent.{Future, ExecutionContext}
import com.ning.http.client.{Response, RequestBuilder}

import com.jasonnerothin.githubclient.MakeLiftJson
import scala.util.{Try, Failure, Success}
import scala.Option
import net.liftweb.json

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
  * Time: 12:08 PM
  * Provides a mechanism for obtaining an OAuth token from github using
  * the shared-secret single authorization pattern described here:
  * http://developer.github.com/v3/oauth/#create-a-new-authorization
  */
trait SingleAuthorization {

  implicit val formats = DefaultFormats

  implicit def futureToEnrichedFuture(future: Future[JsonAST.JValue]): EnrichedFuture[JsonAST.JValue] = new EnrichedFuture[JsonAST.JValue](future)

  /** Request an OAuth token.
    *
    * @param settings OAuth login information
    * @return Option, containing a successful token
    */
  @throws[MappingException]
  def login[E <: HttpExecutor,T](myExecutor: E,
                                 makeJson: (Response => json.JValue) = new MakeLiftJson())
                              (implicit settings: OAuthSettings,
                               executor: ExecutionContext): Option[AuthToken] = {

    val request:RequestBuilder = (host("api.github.com") / "authorizations")
      .secure.as_!(settings.githubUser, settings.githubPassword) <:< Map("Accept" -> "application/json")
    val futureJson = Try(myExecutor(request OK makeJson)) getOrElse Future(JNothing)

    val jObj = futureJson.completeOption match {
      case Some(jo:JObject) => jo
      case _ => JNothing
    }

    Try(jObj.extract[Conversion]) match {
      case Success(c) => Some(AuthToken(c.token, c.id.toInt))
      case Failure(exc) => {
        println(exc.getMessage)
        None
      }
    }

  }

}

protected case class Conversion(token:String, id:String)

object SingleAuthorization extends Object with SingleAuthorization {}
