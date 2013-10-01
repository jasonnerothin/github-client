package com.jasonnerothin.githubclient.api

import com.ning.http.client.{AsyncCompletionHandler, RequestBuilder, Response}
import dispatch.{RequestHandlerTupleBuilder, FunctionHandler}

/** Copyright (c) 2013 jasonnerothin.com
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * Created by IntelliJ IDEA.
  * User: jason
  * Date: 9/26/13
  * Time: 11:23 AM
  */
class ETagExtractor extends (Response => String) {

  def apply(response: Response): String = {
    val list: java.util.List[String] = response.getHeaders("ETag")
    if (list.isEmpty) return ""
    list.get(0)
  }

}

class ETagRequestHandlerTupleBuilder(builder: RequestBuilder, f: (Response => String) = new ETagExtractor)
  extends RequestHandlerTupleBuilder(builder) {

  def GetETag[T](f: Response => String) =
    (builder.build, new FunctionHandler[String](f))

}