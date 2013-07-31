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

This directory (and its subdirectories) contain a number of shell scripts that implement
various parts of the github developer api, using RESTful curl commands. They serve as
simple examples of how to implement the commands in a higher level language like Scala.

Many of these scripts depend upon a "credentials.sh" script, which for obvious reasons,
has not been committed to github. :) The purpose of credentials.sh is to export a number
of variables on which some of the other scripts depend.

