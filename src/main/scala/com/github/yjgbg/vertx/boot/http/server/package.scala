package com.github.yjgbg.vertx.boot
package http

import io.vertx.ext.web.RoutingContext

import scala.reflect.ClassTag

package object server:
  type Method = "GET" | "POST" | "PUT" | "DELETE" | "OPTION" | "PATCH"
  type RequestLine = (Method, String)
  type HttpStatus = 100 | 101 | 102 | 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 300 |
    301 | 302 | 303 | 304 | 305 | 307 | 308 | 400 | 401 | 402 | 403 | 404 | 405 | 406 | 407 |
    408 | 409 | 410 | 411 | 412 | 413 | 414 | 415 | 416 | 417 | 421 | 422 | 423 | 424 | 425 |
    426 | 428 | 429 | 431 | 500 | 501 | 502 | 503 | 504 | 505 | 506 | 507 | 510 | 511
  type ResponseHeaders = Map[String, String]
  type HttpResponse[A] = (HttpStatus, ResponseHeaders, Option[A])
  extension [A:io.circe.Encoder] (httpResponse:HttpResponse[A])
    def use(ctx: RoutingContext) = {
      ctx.response().setStatusCode(httpResponse._1)
      httpResponse._2.foreach(ctx.response().putHeader(_,_))
      import io.circe.syntax.*
      httpResponse._3 match
        case Some(value) => ctx.response().end(value.asJson.noSpaces)
        case None => ctx.response().end()
    }
  object HttpResponse:
    def apply[A](status: HttpStatus = 200,
                 header: ResponseHeaders = Map(),
                 body: A|None.type = None): HttpResponse[A] = body match {
      case None => (status,header,None)
      case _ => (status, header, Some(body.asInstanceOf[A]))
    }
  case class ExceptionHandler[A <: Throwable :ClassTag](predicate: A => Boolean = (it:A) => it.isInstanceOf[A],callback: (RoutingContext,A) => Unit)