package com.jasonnerothin.githubclient.api

import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/31/13
 * Time: 8:33 PM
 */
case class Commit(sha: String, author: Author, message: String, isDistinct: Boolean, url: URL)
case class Author(name: String, email: String)