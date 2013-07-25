package com.jasonnerothin.githubclient

import org.scalatest.FunSuite
import com.jasonnerothin.MyRandom
import java.rmi.RemoteException

/**
 * Created by IntelliJ IDEA.
 * User: jason
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