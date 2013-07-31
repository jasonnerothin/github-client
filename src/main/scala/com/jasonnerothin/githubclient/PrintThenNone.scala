package com.jasonnerothin.githubclient

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
  * Date: 7/23/13
  * Time: 5:29 PM
  * Provides an Option-returning exception printer and companion obj
  */
trait PrintThenNone {

  @throws(classOf[Error])
  def apply(t: Throwable):Option[AnyRef] = {

    t match {
      case r: RuntimeException => printMessage(r)
      case e: Error => throw e
      case _ => printMessage(t)
    }

    None
  }

  protected def printMessage(exc: Throwable) = { println(exc.getMessage) }

}

object PrintThenNone extends Object with PrintThenNone
