#!/bin/bash
set -x

user='jasonnerothin'
repo='github-client'

curl -ik https://api.github.com/users/$user/$repo

exit 0
