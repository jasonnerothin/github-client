package com.jasonnerothin

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
  * Date: 7/17/13
  * Time: 10:55 PM
  */
object MyRandom {

  val rand = new Random

  def randomString(len:Int):String = {
    val buf = new StringBuilder
    for( i <- 0 to len)
      buf.append(nextPrintableChar())
    buf.toString()
  }

  def nextPrintableChar(): Char = {
    val low = 65
    val high = 90
    (rand.nextInt(high - low) + low).toChar
  }

}
