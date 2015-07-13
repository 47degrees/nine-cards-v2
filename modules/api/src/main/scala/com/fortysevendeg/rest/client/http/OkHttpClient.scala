package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions._
import com.fortysevendeg.rest.client.http.Methods._
import com.squareup.okhttp.Headers
import com.squareup.{okhttp => okHttp}
import play.api.libs.json.{Json, Writes}

import scalaz.\/
import scalaz.concurrent.Task

class OkHttpClient(okHttpClient: okHttp.OkHttpClient = new okHttp.OkHttpClient)
  extends HttpClient {

  val jsonMediaType = okHttp.MediaType.parse("application/json")

  val textPlainMediaType = okHttp.MediaType.parse("text/plain")

  override def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(GET, url, httpHeaders)


  override def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(DELETE, url, httpHeaders)


  override def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(POST, url, httpHeaders)


  override def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(POST, url, httpHeaders, Some(Json.toJson(body).toString()))

  def doPostTask[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req): Task[NineCardsException \/ HttpClientResponse] =
    Task {
      \/.fromTryCatchThrowable[HttpClientResponse, NineCardsException] {
        val builder = createBuilderRequest(url, httpHeaders)
        val request = builder.post(createBody(Some(Json.toJson(body).toString()))).build()
        val response = okHttpClient.newCall(request).execute()
        HttpClientResponse(response.code(), Option(response.body()) map (_.string()))
      }
    }

  override def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(PUT, url, httpHeaders)


  override def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): Task[NineCardsException \/ HttpClientResponse] =
    doMethod(PUT, url, httpHeaders, Some(Json.toJson(body).toString()))


  private def doMethod(
    method: Method,
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Option[String] = None
    ): Task[NineCardsException \/ HttpClientResponse] =
    Task {
      fromTryCatchNineCardsException[HttpClientResponse] {
        val builder = createBuilderRequest(url, httpHeaders)
        val request = (method match {
          case GET => builder.get()
          case DELETE => builder.delete()
          case POST => builder.post(createBody(body))
          case PUT => builder.put(createBody(body))
        }).build()
        val response = okHttpClient.newCall(request).execute()
        HttpClientResponse(response.code(), Option(response.body()) map (_.string()))
      }
    }

  private def createBuilderRequest(url: String, httpHeaders: Seq[(String, String)]): okHttp.Request.Builder =
    new okHttp.Request.Builder()
      .url(url)
      .headers(createHeaders(httpHeaders))

  private def createHeaders(httpHeaders: Seq[(String, String)]): Headers = {
    import scala.collection.JavaConverters._
    okHttp.Headers.of(httpHeaders.map(t => t._1 -> t._2).toMap.asJava)
  }

  private def createBody(body: Option[String]) =
    body match {
      case Some(b) => okHttp.RequestBody.create(jsonMediaType, b)
      case _ => okHttp.RequestBody.create(textPlainMediaType, "")
    }

}

object Methods {

  sealed trait Method

  case object GET extends Method

  case object DELETE extends Method

  case object POST extends Method

  case object PUT extends Method

}