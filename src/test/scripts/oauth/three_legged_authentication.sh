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

# 3-LEGGED AUTHENTICATION

# 1. Authorization field: redirects to login form...
# curl -iL https://github.com/login/oauth/authorize?clientId=$CLIENT_ID
# 2. Fake the login form ubmission:
# curl -vic $COOKIE_FILE -b $cookiefile -H "Content-Type: text/html; charset=utf-8" --data-urlencode "login%3Djasonnerothin%26password%3D${PASSWD}%26return_to%3D%2Flogin%2Foauth%2Fauthorize%3FclientId%3D$clientId" https://github.com/session

echo "Still not fully implmented."

exit 1
