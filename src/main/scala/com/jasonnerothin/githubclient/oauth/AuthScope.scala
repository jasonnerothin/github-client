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
  * User: jason
  *
  * Date: 7/12/13
  * Time: 10:57 PM
  * Provides scopes for authorizations
  */
object AuthScope extends Enumeration{

  type AuthScope = Value

  val no_scope, //  public read-only access (includes public user profile info, public repo info, and gists).
      user, // Read/write access to profile info only. Note: this scope includes user:email and user:follow.
      user_email, // Read access to a user’s email addresses.
      user_follow, // Access to follow or unfollow other users.
      public_repo, // Read/write access to public repos and organizations.
      repo, // Read/write access to public and private repos and organizations.
      repo_status, // Read/write access to public and private repository commit statuses. This scope is only necessary to grant other users or services access to private repository commit statuses without granting access to the code. The repo and public_repo scopes already include access to commit status for private and public repositories respectively.
      delete_repo, // Delete access to adminable repositories.
      notifications, // Read access to a user’s notifications. repo is accepted too.
      gist = Value  // Write access to gists

  def default() : AuthScope = no_scope

}
