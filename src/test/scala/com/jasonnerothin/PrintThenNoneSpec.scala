package com.jasonnerothin

import org.scalatest.FunSuite
import java.rmi.RemoteException

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
  * Date: 7/25/13
  * Time: 9:45 AM
  */
class PrintThenNoneSpec extends FunSuite {

  val message = MyRandom.randomString(8)

  test("printThenNone throws Errors"){
    val thrown = intercept[OutOfMemoryError]{
      PrintThenNone(new OutOfMemoryError(message))
    }
    assert(thrown.getMessage === message)
  }

  test("printThenNone returns None for soft exceptions"){
    val actual = PrintThenNone(new IndexOutOfBoundsException(message) )
    assert( actual === None )
  }

  test("printThenNone returns None for hard exceptions"){
    val actual = PrintThenNone(new RemoteException(message))
    assert( actual === None )
  }

}