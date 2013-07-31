package com.jasonnerothin.githubclient

import dispatch._
import net.liftweb.json
import net.liftweb.json.{DefaultFormats, JsonParser}
import com.ning.http.client.Response

/**
 * Created by IntelliJ IDEA.
 * User: jason
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
