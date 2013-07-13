package com.jasonnerothin.githubclient.oauth

import org.scalatest.FunSuite

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/12/13
 * Time: 11:50 PM
 */
class SecretRequestSpec extends FunSuite {

  test("secrets should be kept"){
    val clientId = "clientId"
    val testReq = new SecretRequest(clientId, "foo")

    assert( testReq.clientSecret === testReq.secret )
    assert( testReq.clientId === clientId )

  }

}
