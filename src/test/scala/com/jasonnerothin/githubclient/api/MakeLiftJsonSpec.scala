package com.jasonnerothin.githubclient.api

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import net.liftweb.json.JsonAST._
import com.ning.http.client.Response
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JInt
import net.liftweb.json.JsonAST.JObject

/** Copyright (c) 2013 jasonnerothin.com
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
  * Date: 8/1/13
  * Time: 10:27 PM
  */
class MakeLiftJsonSpec extends FunSuite with MockitoSugar{

  test("Empty response results in JNothing"){
    val actual = new MakeLiftJson({r=>""})(mock[Response])
    assert(actual === JNothing)
  }

  def simpleResponse():String = {
    """
      |{
      |  "person": {
      |    "name": "Joe",
      |    "age": 35,
      |    "spouse": {
      |      "person": {
      |        "name": "Marilyn"
      |        "age": 33
      |      }
      |    }
      |  }
      |}
    """.stripMargin
  }

  test("Simple response returns JValue"){

    val actual = new MakeLiftJson({r=>simpleResponse()})(mock[Response])

    val parsed:List[(_,_)] = for{
      JObject(person) <- actual
      JField("name", JString(name)) <- person
      JField("age", JInt(age)) <- person
    } yield (name,age)

    assert( parsed.length === 2)

    assert( parsed(0)._1 === "Joe")
    assert( parsed(0)._2 === 35)
    assert( parsed(1)._1 === "Marilyn")
    assert( parsed(1)._2 === 33)

  }

}
