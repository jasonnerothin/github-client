package com.jasonnerothin.githubclient.api

import com.jasonnerothin.githubclient.oauth._
import org.joda.time.DateTime
import java.net.URL
import dispatch.{Http, HttpExecutor}

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
  def apply(tag: Option[RepositoryEventTag], http: HttpExecutor = Http)
                    (implicit settings: OAuthSettings, token: Option[AuthToken] = None, authCheck: CheckAuthorization = CheckAuthorization): Pair[RepositoryEventTag, List[RepositoryEvent]] = {
    if (!repositoryIsPublic) require(token.isDefined && authCheck.authorized(token.get), "Not authorized to get repository events.")
    for( et:RepositoryEventType.Value <- supportedEventTypes() ){
      println( et.toString )
    }
    Pair(RepositoryEventTag("eTag"), List())
  }

  private def supportedEventTypes(): List[RepositoryEventType.Value] = List(RepositoryEventType.DeleteEvent, RepositoryEventType.ForkApplyEvent, RepositoryEventType.PushEvent)

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