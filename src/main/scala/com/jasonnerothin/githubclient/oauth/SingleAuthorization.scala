package com.jasonnerothin.githubclient.oauth

import dispatch._

import net.liftweb.json._
import scala.concurrent.ExecutionContext
import com.ning.http.client.{AsyncHandler, Request, RequestBuilder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import com.jasonnerothin.githubclient.{MakeLiftJson, PrintAndNone}

/**
 * Created by IntelliJ IDEA.
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
  def login(http: Http, makeJson:MakeLiftJson = new MakeLiftJson())(implicit settings: OAuthSettings, executor: ExecutionContext): Option[AuthToken] = {

    val reqBuilder: RequestBuilder = url("https://api.github.com/authorizations")
      .secure.as_!(settings.githubUser, settings.githubPassword) <:< Map("Accept" -> "application/json")
    val fjk:(Request, AsyncHandler[JValue]) = reqBuilder OK makeJson
    val response = http.apply(fjk)

    val parsed = for {jv <- response} yield jv
    val p2 = parsed map{ p =>
      p.extract[AuthToken]
    }

    val tok = for {
      tryOpt <- p2.value
      if tryOpt.isSuccess
    } yield tryOpt.get

    tok match {
      case Some(AuthToken(_,_)) => tok
      case _ => None
    }

  }

}

object SingleAuthorization extends Object with SingleAuthorization {}
