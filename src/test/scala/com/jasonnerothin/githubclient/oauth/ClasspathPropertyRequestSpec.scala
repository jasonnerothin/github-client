package com.jasonnerothin.githubclient.oauth

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import java.io.{InputStreamReader, Reader, InputStream}
import org.mockito.Matchers._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer._
import org.mockito.Mockito._
import org.mockito.stubbing.Answer
import org.mockito.{Matchers, Mockito}
import java.nio.charset.Charset

/**
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 7/13/13
  * Time: 7:57 AM
  */
class ClasspathPropertyRequestSpec extends FunSuite with MockitoSugar{

  val name1 = "clientSecret"
  val name2 = "clientId"
  val prop1 = "prop1"
  val prop2 = "prop3"
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

    val classpathPropertyRequest = new ClasspathPropertyRequest(mockProps)

    val p1 = classpathPropertyRequest.getProperty(name1)
    assert( p1 === prop1 )
    val p2 = classpathPropertyRequest.getProperty(name2)
    assert( p2 === prop2 )

  }

}
