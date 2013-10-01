package com.jasonnerothin.githubclient.api

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

import com.jasonnerothin.githubclient.oauth._
import com.jasonnerothin.githubclient.api._
import com.jasonnerothin.githubclient._
import org.joda.time.DateTime
import org.slf4j.{LoggerFactory, Logger}
import java.net.URL
import com.ning.http.client.{RequestBuilder, Response}
import net.liftweb.json
import scala.util.{Failure, Success, Try}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.{JsonAST, DefaultFormats}
import com.jasonnerothin.githubclient.oauth.AuthToken
import scala.Some
import dispatch._

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
  * Date: 7/16/13
  * Time: 9:37 PM
  * Provides a mechanism for getting repository events: see
  * http://developer.github.com/v3/activity/events/#list-repository-events for more info...
  */
class GetRepositoryEvents(val repositoryName: String, repositoryIsPublic: Boolean = false) {

  implicit val Formats = DefaultFormats
  private val logger: Logger = LoggerFactory.getLogger(classOf[GetRepositoryEvents])

  /** Check the repository for changes. So that unnecessary charges aren't incurred at github,
    * it is advisable to pass an eventTag, since it enables us to check for changes
    * intermittently - and for free - by keeping track of an eTag header sent back by github.
    *
    * The recommended procedure is to call the first time with no tag and then keep track of the
    * tag that's handed back for all subsequent calls. The client should also only wait to try
    * again ``RepositoryEventTag#xPollInterval`` seconds
    *
    * Returns a partial list of repository events. Currently, only ``#supportedEventTypes``
    * types are supported (returned). This backing HTTP request will be invoked no sooner than
    * tag.lastChecked + tag.xPollIntervalInSecs.
    *
    * @param tag header tracking info about the state of the system at the LAST check
    * @param token must be authenticated if the repository is private, otherwise None is fine
    * @param settings required authentication settings
    * @param authCheck optional check object
    * @return a Pair, containing a RepositoryEventTag and zero or more RepositoryEvents
    */
  def apply(tag: Option[RepositoryEventTag], http: HttpExecutor = Http, makeJson: (Response => json.JValue) = new MakeLiftJson())
                    (implicit settings: OAuthSettings, token: Option[AuthToken] = None, authCheck: CheckAuthorization = CheckAuthorization)
            : Pair[RepositoryEventTag, List[RepositoryEvent]] = {

    if (!repositoryIsPublic) require(token.isDefined && authCheck.authorized(token.get)(settings), "Not authorized to get repository events.")
    logger.debug("Supported event types:")
    for( et:RepositoryEventType.Value <- supportedEventTypes() ){
      logger.debug( et.toString )
    }

    def baseRequest():RequestBuilder = {
      url("https://api.github.com/repos/%s/%s/events".format(repositoryName,settings.githubUser))
        .secure.as_!(settings.githubUser, settings.githubPassword) <:< acceptJsonHeader()
    }

    val request = tag match {
      case Some(t) =>  baseRequest() <:< Map("ETag" -> t.eTag)
      case None => baseRequest()
    }

//    val g = GetETag
    request OK makeJson

    val e = new ETagExtractor // Response -> String (etag)
    val f = makeJson // Response -> json.JValue

    val eTag = for( tag <- http( request > e ) ) yield tag

//    val json = http(request OK makeJson )
//    json match {
//      case Success(JArray(t)) => t
//      case Failure(e) => {
//        logger.error(e.getMessage)
//        JNothing
//      }
//    }

    Pair(RepositoryEventTag(eTag.completeOption.getOrElse("NONE")), List()) // dummy return val

  }

  private def supportedEventTypes(): List[RepositoryEventType.Value] = List(
    RepositoryEventType.CreateEvent
    , RepositoryEventType.DeleteEvent
    , RepositoryEventType.ForkApplyEvent
    , RepositoryEventType.PushEvent
  )

}

/** @param eTag value returned with Etag header
  * @param xPollIntervalInSecs time you must before checking again (without incurring a charge)
  * @param followLinks paged links that contain events
  * @param lastChecked time of last check
  */
case class RepositoryEventTag(eTag: String, xPollIntervalInSecs: Int = 60, followLinks: List[URL] = List(), lastChecked: DateTime = new DateTime())

/** see http://developer.github.com/v3/activity/events/
  * class for encapsulating the most important information returned from a call to ``GetRepositoryEvents#apply``
  * @param repositoryEventType event classification
  * @param eventId github key
  * @param isPublic whether the event is publicly available
  * @param timeOf time of the event
  * @param commits list of commits pertaining to this event
  */
case class RepositoryEvent(repositoryEventType: RepositoryEventType.Value, eventId: BigInt, isPublic: Boolean = false, timeOf: DateTime, commits: List[CommitInfo] = List())

/** see http://developer.github.com/v3/activity/events/types/
  */
object RepositoryEventType extends Enumeration {
  type RepositoryEventType = Value

  val CommitCommentEvent,
  CreateEvent,
  DeleteEvent,
  DownloadEvent,
  FollowEvent,
  ForkEvent,
  ForkApplyEvent,
  GistEvent,
  GollumEvent,
  IssueCommentEvent,
  IssuesEvent,
  MemberEvents,
  PublicEvent,
  PullRequestEvent,
  PullRequestReviewCommentEvent,
  PushEvent,
  TeamAddEvent,
  WatchEvent = Value

}