#!/bin/bash -x

. ./creds.sh # exports ${USER}, ${USER}, $CLIENT_ID and $CLIENT_SECRET

if [ -z ${CLIENT_SECRET} ];
then
   echo "ERROR: Please provide a creds.sh script that defines authentication info."
   exit 1
fi

COOKIE_FILE='cookies.txt'

# 3-LEGGED AUTHENTICATION

# 1. Authorization field: redirects to login form...
# curl -iL https://github.com/login/oauth/authorize?clientId=$CLIENT_ID
# 2. Fake the login form ubmission: 
# curl -vic $COOKIE_FILE -b $cookiefile -H "Content-Type: text/html; charset=utf-8" --data-urlencode "login%3Djasonnerothin%26password%3D${PASSWD}%26return_to%3D%2Flogin%2Foauth%2Fauthorize%3FclientId%3D$clientId" https://github.com/session

# SIMPLE AUTHENTICATION

base_url="https://api.github.com"
sed "s/CLIENT_ID/$CLIENT_ID/g;s/CLIENT_SECRET/$CLIENT_SECRET/g" template.json > output.json

curl -vic -H "Accept: application/json" -H "Content-type: application/json" -X POST -u ${USER}:${PASSWD} -d @output.json $base_url/authorizations
rm output.json

exit 0
