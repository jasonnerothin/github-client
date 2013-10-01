package com.jasonnerothin

import org.scalatest.FunSuite
import com.jasonnerothin.githubclient.oauth.CheckAuthorization
import dispatch.Http
import scala.util.Random

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
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 9/25/13
  * Time: 2:07 PM
  */
class MockHttpSugarTest extends FunSuite with MockHttpSugar{

  val defaultSettings = $oAuthSettings()
  val defaultToken = $authToken()
  val defaultHttp = $http()
  val defaultTimeout = 0L

  def systemUnderTest(checkAuth: CheckAuthorization, http: Http, timeout: Long): Boolean = {
    checkAuth.authorized(defaultToken, http, timeout)(settings = defaultSettings)
  }

  test("CheckAuthorization mock returns true"){

    val checkAuth:CheckAuthorization = $checkAuthorization(isAuthorized = true, http = defaultHttp, timeout = defaultTimeout)

    assert( java.lang.Boolean.TRUE === systemUnderTest(checkAuth, defaultHttp, defaultTimeout))

  }

  test("CheckAuthorization mock returns false"){

    val checkAuth:CheckAuthorization = $checkAuthorization(isAuthorized = false)

    assert( java.lang.Boolean.FALSE === systemUnderTest(checkAuth, $http(), Math.abs(new Random(System.currentTimeMillis()).nextInt())))

  }

  test("CheckAuthorization returns when default params are used")(pending)
  def flob(): Unit = {

    assert(java.lang.Boolean.TRUE === $checkAuthorization(isAuthorized = true).authorized(defaultToken)(defaultSettings))
    assert(java.lang.Boolean.FALSE === $checkAuthorization(isAuthorized = false).authorized(defaultToken)(defaultSettings))

  }

}
