#!/bin/bash
#
# copyright (c) 2013 jasonnerothin.com
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# http://developer.github.com/v3/activity/events/

. ./credentials.sh

if [ -z ${CLIENT_SECRET} ];
then
   echo "ERROR: Please provide a credentials.sh script that defines authentication info."
   exit 1
fi

REPO=sillytestrepo
URL_APP="https://api.github.com/repos/$USER/$REPO/events -u ${CLIENT_ID}:${CLIENT_SECRET}" # private repo - connecting AS AN APPLICATION (I've not seen this actually work.)
URL_USER="https://api.github.com/repos/$USER/$REPO/events -u ${USER}:${PASSWD}" # private repo - connecting AS YOURSELF
URL_PUBLIC=https://api.github.com/repos/$USER/$REPO/events # public repo
ETAG=\"3daa27c42e4cdfd0252f0b8c5ee3f9f9\"
X_POLL_INTERVAL=60

# Initial request: You are *required* to grab ETag and
# X-Poll-Interval from the response headers

echo #### INITIAL REQUEST ####
echo curl $URL_USER -iv
# curl $URL0 -iv

#Example response:
#> HTTP/1.1 200 OK
#> Server: GitHub.com
#> Date: Wed, 31 Jul 2013 15:39:51 GMT
#> Content-Type: application/json; charset=utf-8
#> Status: 200 OK
#> X-RateLimit-Limit: 60
#> X-RateLimit-Remaining: 59
#> X-RateLimit-Reset: 1375288791
#> Cache-Control: public, max-age=60, s-maxage=60
#> Last-Modified: Wed, 31 Jul 2013 15:25:23 GMT
#> ETag: "2f71dbbdd9f4cce70=90b4949444e9e925"
#> X-Poll-Interval: 60
#> Vary: Accept
#> X-GitHub-Media-Type: github.beta
#> Link: <https://api.github.com/user/449078/events?page=2>; rel="next"
#> X-Content-Type-Options: nosniff
#> Content-Length: 40352
#> Access-Control-Allow-Credentials: true
#> Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
#> Access-Control-Allow-Origin: *
#> Vary: Accept-Encoding

#echo $ETAG
echo checking url @ `date` ....
START=$(date +"%s")
TIMEOUT=$(($START + $X_POLL_INTERVAL))

echo #### FOLLOW UP REQUEST ####
echo curl $URL_PUBLIC -H 'If-None-Match: "3daa27c42e4cdfd0252f0b8c5ee3f9f9"' -H "Accept: application/json" -iv
# curl $URL_PUBLIC -H 'If-None-Match: "3daa27c42e4cdfd0252f0b8c5ee3f9f9"' -H "Accept: application/json" -iv

#> HTTP/1.1 200 OK
#> Server: GitHub.com
#> Date: Wed, 31 Jul 2013 17:29:09 GMT
#> Content-Type: application/json; charset=utf-8
#> Status: 200 OK
#> X-RateLimit-Limit: 5000
#> X-RateLimit-Remaining: 4999
#> X-RateLimit-Reset: 1375295349
#> Cache-Control: private, max-age=60, s-maxage=60
#> Last-Modified: Thu, 27 Jun 2013 02:08:22 GMT
#> ETag: "3daa27c42e4cdfd0252f0b8c5ee3f9f9" !!!!!!!! CHANGED !!!!!!!!!
#> X-Poll-Interval: 60
#> Vary: Accept, Authorization, Cookie
#> X-GitHub-Media-Type: github.beta
#> Link: <https://api.github.com/repositories/10984736/events?page=2>; rel="next"
#> X-Content-Type-Options: nosniff
#> Content-Length: 787
#> Access-Control-Allow-Credentials: true
#> Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
#> Access-Control-Allow-Origin: *
#> Vary: Accept-Encoding

CAPTURED_RSRC="/repositories/10984736/"
EVT_URL="https://api.github.com${CAPTURED_RSRC}events?page=1"

echo #### GET THE EVENTS ####
echo curl $EVT_URL -H 'If-None-Match: "3daa27c42e4cdfd0252f0b8c5ee3f9f9"' -H "Accept: application/json" -iv
# curl $EVT_URL -H 'If-None-Match: "3daa27c42e4cdfd0252f0b8c5ee3f9f9"' -H "Accept: application/json" -iv

#> HTTP/1.1 200 OK
#> Server: GitHub.com
#> Date: Wed, 31 Jul 2013 19:10:15 GMT
#> Content-Type: application/json; charset=utf-8
#> Status: 200 OK
#> X-RateLimit-Limit: 60
#> X-RateLimit-Remaining: 59
#> X-RateLimit-Reset: 1375301415
#> X-Poll-Interval: 60
#> X-GitHub-Media-Type: github.beta
#> Link: <https://api.github.com/repositories/10984736/events?page=1>; rel="prev"
#> X-Content-Type-Options: nosniff
#> Content-Length: 5
#> Access-Control-Allow-Credentials: true
#> Access-Control-Expose-Headers: ETag, Link, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes
#> Access-Control-Allow-Origin: *
#> Vary: Accept-Encoding
#> [
#>  {
#>    "id": "1793559780",
#>    "type": "PushEvent",
#>    "actor": {
#>      "id": 449078,
#>      "login": "jasonnerothin",
#>      "gravatar_id": "84d1bd6afe9ff079d54783756a90b07c",
#>      "url": "https://api.github.com/users/jasonnerothin",
#>      "avatar_url": "https://secure.gravatar.com/avatar/84d1bd6afe9ff079d54783756a90b07c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
#>    },
#>    "repo": {
#>      "id": 10984736,
#>      "name": "jasonnerothin/sillytestrepo",
#>      "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo"
#>    },
#>    "payload": {
#>      "push_id": 208493932,
#>      "size": 1,
#>      "distinct_size": 1,
#>      "ref": "refs/heads/master",
#>      "head": "df67aa9a04471de24c3a74d049b8ac82d415c2b0",
#>      "before": "5e63c95aa2fd570aa2dbf6d2383d6f5446c44d3f",
#>      "commits": [
#>        {
#>          "sha": "df67aa9a04471de24c3a74d049b8ac82d415c2b0",
#>          "author": {
#>            "email": "jason.nerothin@gmail.com",
#>            "name": "Jason Nerothin"
#>          },
#>          "message": "adding an ignore file",
#>          "distinct": true,
#>          "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo/commits/df67aa9a04471de24c3a74d049b8ac82d415c2b0"
#>        }
#>      ]
#>    },
#>    "public": true,
#>    "created_at": "2013-07-31T20:00:32Z"
#>  },
#>  {
#>    "id": "1766928187",
#>    "type": "CreateEvent",
#>    "actor": {
#>      "id": 449078,
#>      "login": "jasonnerothin",
#>      "gravatar_id": "84d1bd6afe9ff079d54783756a90b07c",
#>      "url": "https://api.github.com/users/jasonnerothin",
#>      "avatar_url": "https://secure.gravatar.com/avatar/84d1bd6afe9ff079d54783756a90b07c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
#>    },
#>    "repo": {
#>      "id": 10984736,
#>      "name": "jasonnerothin/sillytestrepo",
#>      "url": "https://api.github.com/repos/jasonnerothin/sillytestrepo"
#>    },
#>    "payload": {
#>      "ref": null,
#>      "ref_type": "repository",
#>      "master_branch": "master",
#>      "description": ""
#>    },
#>    "public": true,
#>    "created_at": "2013-06-27T02:08:22Z"
#>  }
#> ]


NOW=$(date +"%s")
echo "Waiting til we can connect again..."
while [ $NOW -lt $TIMEOUT ]; do
    echo In $(($TIMEOUT - $NOW)) secs...
    sleep 6
    NOW=$(date +"%s")
done

exit 0