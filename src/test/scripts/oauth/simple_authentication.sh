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

. ./credentials.sh # exports ${USER}, ${USER}, $CLIENT_ID and $CLIENT_SECRET

if [ -z ${CLIENT_SECRET} ];
then
   echo "ERROR: Please provide a credentials.sh script that defines authentication info."
   exit 1
fi

# SIMPLE AUTHENTICATION

base_url="https://api.github.com"
sed "s/CLIENT_ID/$CLIENT_ID/g;s/CLIENT_SECRET/$CLIENT_SECRET/g" simple_authentication.template.json > simple_authentication.json

curl -H "Accept: application/json" -H "Content-type: application/json" -X POST -u ${USER}:${PASSWD} -d @simple_authentication.json -vi $base_url/authorizations
rm simple_authentication.json

exit 0
