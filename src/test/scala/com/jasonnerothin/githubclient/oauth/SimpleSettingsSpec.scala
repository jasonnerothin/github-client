package com.jasonnerothin.githubclient.oauth

import org.scalatest.FunSuite
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
  * Date: 7/12/13
  * Time: 11:50 PM
  */
class SimpleSettingsSpec extends FunSuite {

  val rand = new Random

  test("secrets should be kept"){
    val clientId = rand.nextString(3)
    val secret = rand.nextString(3)
    val uname = rand.nextString(4)
    val pw = rand.nextString(5)
    val testReq = new SimpleSettings(clientId, secret, uname, pw)

    assert( testReq.clientSecret === secret )
    assert( testReq.clientId === clientId )
    assert( testReq.githubUser === uname )
    assert( testReq.githubPassword === pw )

  }

}
