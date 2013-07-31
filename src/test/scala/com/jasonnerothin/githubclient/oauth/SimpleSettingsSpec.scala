package com.jasonnerothin.githubclient.oauth

import org.scalatest.FunSuite
import scala.util.Random

/**
  * Copyright (c) 2013 jasonnerothin.com
  * User: jason
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
