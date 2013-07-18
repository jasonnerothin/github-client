#!/bin/bash -x

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
