package com.jasonnerothin

import scala.util.Random

/**
  * Copyright (c) 2013 jasonnerothin.com
  * User: jason
  * Date: 7/17/13
  * Time: 10:55 PM
  */
object MyRandom {

  val rand = new Random

  def randomString(len:Int):String = {
    val buf = new StringBuilder
    for( i <- 0 to len)
      buf.append(nextPrintableChar())
    buf.toString()
  }

  def nextPrintableChar(): Char = {
    val low = 65
    val high = 90
    (rand.nextInt(high - low) + low).toChar
  }

}
