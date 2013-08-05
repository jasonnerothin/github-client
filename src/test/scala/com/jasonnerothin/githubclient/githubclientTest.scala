package com.jasonnerothin.githubclient

import org.scalatest.FunSuite

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/13
 * Time: 8:14 AM
 */
class githubclientTest extends FunSuite{

  private val forbiddenCharacters = ':' :: ',' :: '=' :: ';' :: '\t' :: '\r' :: '\n':: '\r' :: '\f' :: Nil

  /** Testing the test infrastructure
    */
  test("Forbidden character list handles end-lines correctly."){

    class SpecificException extends RuntimeException{
      override def getMessage = "end-line is properly escaped."
    }

    val se = intercept[SpecificException]{
      "hello\nsquirreled" foreach(ch => if(forbiddenCharacters.contains(ch)) throw new SpecificException())
    }

    assert(se.isInstanceOf[SpecificException]) // not Nothing or null or something
  }

  test("No forbidden characters in header names."){
    // =,;: \t\r\n\v\f:

    acceptJsonHeader().keys.foreach(key=>
      key.foreach(ch => if( forbiddenCharacters.contains( ch )) assert(false, "Forbidden character encountered in header name: [%s]".format(ch.toString)))
    )
  }


}
