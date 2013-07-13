package com.jasonnerothin.githubclient.oauth

import org.scalatest.FunSuite

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/12/13
 * Time: 11:04 PM
 */
class AuthScopeSpec extends FunSuite{

  test("default is no_scope"){
    assert( AuthScope.default === AuthScope.no_scope )
    info("default scope is no_scope")
  }

}
