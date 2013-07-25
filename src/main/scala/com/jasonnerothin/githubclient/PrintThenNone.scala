package com.jasonnerothin.githubclient

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/23/13
 * Time: 5:29 PM
 * Provides an Option-returning exception printer and companion obj
 */
trait PrintThenNone {

  @throws(classOf[Error])
  def apply(t: Throwable):Option[AnyRef] = {

    t match {
      case r: RuntimeException => printMessage(r)
      case e: Error => throw e
      case _ => printMessage(t)
    }

    None
  }

  protected def printMessage(exc: Throwable) = { println(exc.getMessage) }

}

object PrintThenNone extends Object with PrintThenNone
