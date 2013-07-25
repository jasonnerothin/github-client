package com.jasonnerothin.githubclient

import com.ning.http.client.Response
import dispatch._
import net.liftweb.json._
import dispatch.as.lift._
import net.liftweb.json
import json._

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/23/13
 * Time: 8:07 PM
 * Allows us to mix in a mock responder in place of as.String
 */
class MakeLiftJson(val responder: (Response => String) = as.String) extends (Response => JValue) {

  def apply(response: Response) = {
    (responder andThen JsonParser.parse)(response)
  }

}
