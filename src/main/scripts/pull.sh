#!/bin/bash # -x

. ./creds.sh # source of CLIENT_ID, CLIENT_SECRET, USER and PASSWD variables

# curl -ik https://api.github.com/users/$user/$repo
curl -ikL https://github.com/login/oauth/authorize?clientId=${CLIENT_ID}

exit 0
