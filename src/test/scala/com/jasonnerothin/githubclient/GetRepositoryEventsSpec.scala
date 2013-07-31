package com.jasonnerothin.githubclient

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import com.jasonnerothin.MockHttpSugar

/**
  * Copyright (c) 2013 jasonnerothin.com
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * Date: 7/18/13
  * Time: 1:33 PM
  */
class GetRepositoryEventsSpec extends FunSuite with MockHttpSugar{


  test("you can't get repository events if you aren't authorized"){

    val notAuthorized = $checkAuthorization(isAuthorized = false)
    val settings = $oAuthSettings()

    val getEvents = new GetRepositoryEvents("repoName")

    val caught = intercept[IllegalArgumentException] {
      getEvents.call($authToken(), None, None)(settings, notAuthorized)
    }

    val pattern = ".*Not authorized.*"
    val msg = caught.getMessage

    assert( msg.matches(pattern), "Unexpected error message: [%s]".format(msg))

  }

}
