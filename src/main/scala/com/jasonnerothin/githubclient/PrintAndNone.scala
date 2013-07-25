package com.jasonnerothin.githubclient

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/23/13
 * Time: 5:29 PM
 * Provides an Option-returning exception printer and companion obj
 */
trait PrintAndNone {

  def apply(t: Throwable) = {
    println(t.getMessage)
    None
  }

}

object PrintAndNone extends Object with PrintAndNone{}
