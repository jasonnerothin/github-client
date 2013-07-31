#!/bin/bash # -x
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

# All values in this file are made up, but approximately realistic.
# Rename this file to "credentials.sh" and reset the values appropriately.
# Then your other scripts should work.

export USER='your-github-userid'
export PASSWD='your-github-passwd'

# These two come from the github ui:
# http://github.com -> your account -> settings -> applications ->
# Register new application

export CLIENT_ID='607c8ad6d45894be6a43'
export CLIENT_SECRET='9a6541d54507a806b5a20c0d81234d4fe134fff8'

# personal api access tokens
#
# http://github.com -> your account -> settings -> applications ->
# "personal api access tokens" -> create

export PERSONAL_TOKEN='a2f484a7db810319b67919afe71fd69dcc8f3ae7'

# github_client token
#
# For example, the token returned in a response from simple_authentication.sh ...
#
# http://githubm.com -> your account -> settings -> applications ->
# "Authorized Applications"

export TOKEN='79ad9d4d7cb9740dc72dd580711b9c0f631ad01b'

