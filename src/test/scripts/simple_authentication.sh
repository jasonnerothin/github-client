#!/bin/bash -x

. ./credentials.sh # exports ${USER}, ${USER}, $CLIENT_ID and $CLIENT_SECRET

if [ -z ${CLIENT_SECRET} ];
then
   echo "ERROR: Please provide a creds.sh script that defines authentication info."
   exit 1
fi

COOKIE_FILE='simple-auth-cookies.txt'

# SIMPLE AUTHENTICATION

base_url="https://api.github.com"
sed "s/CLIENT_ID/$CLIENT_ID/g;s/CLIENT_SECRET/$CLIENT_SECRET/g" simple_authentication.template.json > output.json

curl -H "Accept: application/json" -H "Content-type: application/json" -X POST -u ${USER}:${PASSWD} -d @output.json -vi $base_url/authorizations
rm output.json

exit 0
