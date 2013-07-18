#!/bin/bash

# 3-LEGGED AUTHENTICATION

# 1. Authorization field: redirects to login form...
# curl -iL https://github.com/login/oauth/authorize?clientId=$CLIENT_ID
# 2. Fake the login form ubmission:
# curl -vic $COOKIE_FILE -b $cookiefile -H "Content-Type: text/html; charset=utf-8" --data-urlencode "login%3Djasonnerothin%26password%3D${PASSWD}%26return_to%3D%2Flogin%2Foauth%2Fauthorize%3FclientId%3D$clientId" https://github.com/session

echo "Still not fully implmented."

exit 1
