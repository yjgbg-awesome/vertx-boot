package com.github.yjgbg.vertx.boot
package http.server

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

import scala.concurrent.duration.Duration

case class Middleware(requestLine: Seq[RequestLine] = Seq(),
                      callback:RoutingContext => Unit = ctx => ctx.next())

object Middleware:
  def timeout[A: io.circe.Encoder](requestLine: Seq[RequestLine],
                                   duration: Duration,
                                   defaultHttpResponse: HttpResponse[A]): Middleware =
    apply(requestLine, ctx => {
      val id = ctx.vertx().setTimer(duration.toMillis,
        ignored => if (!ctx.response().ended()) defaultHttpResponse.use(ctx))
      ctx.response().endHandler(ignored => ctx.vertx().cancelTimer(id))
      ctx.next()
    })