package com.jasonnerothin.githubclient.oauth

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import scala.util.Random
import java.io.InputStreamReader
import org.mockito.Matchers._
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mockito._
import org.mockito.stubbing.Answer

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
  * Date: 7/13/13
  * Time: 7:57 AM
  */
class ClasspathPropertySettingsSpec extends FunSuite with MockitoSugar{

  val rand = new Random
  val name1 = rand.nextString(4)
  val name2 = rand.nextString(3)
  val prop1 = rand.nextString(6)
  val prop2 = rand.nextString(7)
  val testContent = name1 + "=" + prop1 + "\n" + name2 + "=" + prop2 + "\n"

  def mockProps = {
    // string -> byte
    def byteArr(str: String): Array[Byte] = {
      val charArr = str.toCharArray
      var byteList = List[Byte]()
      for( c <- charArr) byteList = byteList :+ c.toByte
      byteList.toArray
    }
    val bytes = byteArr(testContent)
    // deal with pass-by-reference by writing into the provided (i.e. target) array
    def expectedAnswer(): Answer[Int] = {
      new Answer[Int](){
        override def answer(invocation: InvocationOnMock) = {
          val arr = invocation.getArguments()(0).asInstanceOf[Array[Char]]
          for( i <- 0 to bytes.length -1 ) arr(i) = bytes(i).toChar
          val len = bytes.length
          len
        }
      }
    }
    // mock it
    val stream = mock[InputStreamReader]
    doAnswer(expectedAnswer()).when(stream).read(isA(classOf[Array[Char]]))
    doAnswer(expectedAnswer()).when(stream).read(isA(classOf[Array[Char]]), isA(classOf[Int]), isA(classOf[Int]))
    var stub = when(stream.read())
    for( b <- bytes ) stub = stub.thenReturn(b.toInt)
    stub.thenReturn(0).thenThrow(new IllegalStateException("too many calls"))
    stream
  }

  test("getPropertyDoesGetTheRightProperty")(pending)

  /**
    * I have a strong suspicion that whoever wrote the horrible, unreadable, procedural,
    * un-mockable, garbled "API" known as InputStreamReader and InputStream is also the
    * person responsible for screwing the the design of Java around generics.
    */
  def fixMe(){

    val classpathPropertyRequest = new ClasspathPropertySettings(mockProps)

    val p1 = classpathPropertyRequest.getProperty(name1)
    assert( p1 === prop1 )
    val p2 = classpathPropertyRequest.getProperty(name2)
    assert( p2 === prop2 )

  }

}
