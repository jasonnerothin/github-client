#!/bin/bash -x
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

# http://developer.github.com/v3/oauth/#check-an-authorization

. ./credentials.sh # exports ${USER}, ${USER}, ${CLIENT_ID}, ${CLIENT_SECRET} and ${TOKEN}

if [ -z ${CLIENT_SECRET} ];
then
   echo "ERROR: Please provide a credentials.sh script that exports authentication info."
   exit 1
fi

COOKIE_FILE='check-auth-cookies.txt'

base_url="https://api.github.com"

# // /applications/:client_id/tokens/:access_token
curl -vicL -H "Accept: application/json" -X GET -u ${CLIENT_ID}:${CLIENT_SECRET} $base_url/applications/${CLIENT_ID}/tokens/${TOKEN}

exit 0
