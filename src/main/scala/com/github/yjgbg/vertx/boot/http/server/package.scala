package com.github.yjgbg.vertx.boot
package http

import io.vertx.core.Future
import io.vertx.ext.web.RoutingContext

import cps.async
import vertxCps.CpsSyntax.given
package object server:
  type Method = "GET" | "POST" | "PUT" | "DELETE" | "OPTION" | "PATCH"
  type RequestLine = (Method, String)
  type HttpStatus = 100 | 101 | 102 | 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 300 |
    301 | 302 | 303 | 304 | 305 | 307 | 308 | 400 | 401 | 402 | 403 | 404 | 405 | 406 | 407 |
    408 | 409 | 410 | 411 | 412 | 413 | 414 | 415 | 416 | 417 | 421 | 422 | 423 | 424 | 425 |
    426 | 428 | 429 | 431 | 500 | 501 | 502 | 503 | 504 | 505 | 506 | 507 | 510 | 511
  type ResponseHeaders = Map[String, String]
  type HttpResponse[A] = (HttpStatus, ResponseHeaders, Option[A])|Option[A]|A
  extension [A] (httpResponse:HttpResponse[A])
    def use(ctx: RoutingContext)(using encoder: io.circe.Encoder[A]):Future[Unit] = async {
      httpResponse match
        case (status: HttpStatus,headers,Some(body)) =>
          ctx.response().setStatusCode(status)
          headers.asInstanceOf[ResponseHeaders].foreach(ctx.response().putHeader(_,_))
          ctx.response().end(encoder(body.asInstanceOf[A]).noSpaces)
        case (status: HttpStatus,headers,None) =>
          ctx.response().setStatusCode(status)
          headers.asInstanceOf[ResponseHeaders].foreach(ctx.response().putHeader(_,_))
          ctx.response().end()
        case Some(body) => ctx.response().setStatusCode(200)
          .putHeader("Content-Type","application/json")
          .end(encoder(body.asInstanceOf[A]).noSpaces)
        case None => ctx.response().setStatusCode(200).end()
        case body => ctx.response().setStatusCode(200)
          .putHeader("Content-Type","application/json")
          .end(encoder(body.asInstanceOf[A]).noSpaces)
    }
  object HttpResponse:
    def apply[A](status: HttpStatus = 200,
                 header: ResponseHeaders = Map(),
                 body: A|None.type = None): HttpResponse[A] = body match {
      case None => (status,header,None)
      case _ => (status, header, Some(body.asInstanceOf[A]))
    }
  case class ExceptionHandler[A <: Throwable]
  (
    order:Int = org.springframework.core.Ordered.LOWEST_PRECEDENCE,
    predicate: Throwable => Boolean = _ => true,
    callback: (RoutingContext,A) => Unit
  )extends org.springframework.core.Ordered:
    override def getOrder: Int = order