package com.jasonnerothin.githubclient.oauth

import dispatch._

import net.liftweb.json._
import scala.concurrent.{Future, ExecutionContext}
import com.ning.http.client.{Response, Request, RequestBuilder}

import com.jasonnerothin.githubclient.MakeLiftJson
import scala.util.{Try, Failure, Success}
import scala.Option
import net.liftweb.json

/**
  * Copyright (c) 2013 jasonnerothin.com
  * User: jason
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
    val futureJson = myExecutor.apply(request OK makeJson)

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
