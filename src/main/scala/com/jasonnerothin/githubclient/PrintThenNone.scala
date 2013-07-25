package com.jasonnerothin.githubclient

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/23/13
 * Time: 5:29 PM
 * Provides an Option-returning exception printer and companion obj
 */
trait PrintThenNone {

  def apply(t: Throwable) = {

    case r: RuntimeException => printMessage(r)
    case e: Error => throw e
    case _ => printMessage(t)

    None
  }

  protected def printMessage(exc: Throwable) = { println(exc.getMessage) }

}

object printThenNone extends Object with PrintThenNone
