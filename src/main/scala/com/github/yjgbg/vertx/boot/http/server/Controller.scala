package com.github.yjgbg.vertx.boot
package http.server

import io.vertx.core.http.{Cookie, HttpMethod}
import io.vertx.core.json.Json
import io.vertx.core.{Future, Handler, MultiMap}
import io.vertx.ext.web.RoutingContext

import scala.reflect.ClassTag

import cps.{async,await}
import syntax.CpsSyntax.given_VertxCpsMonad

case class Controller[A: io.circe.Decoder, B: io.circe.Encoder]
(requestLine: Seq[RequestLine], callback: HttpRequest[A] => Future[HttpResponse[B]]):
  import cps.{async,await}
  def toHandler(exceptionHandler: java.util.List[ExceptionHandler[Throwable]]): Handler[RoutingContext] =
    ctx => callback(HttpRequest(ctx))
      .map(httpResponse => {
        ctx.response().setStatusCode(httpResponse._1)
        httpResponse._2.foreach(ctx.response().putHeader(_,_))
        httpResponse._3 match
          case Some(value) => ctx.response().end(summon[io.circe.Encoder[B]](value).noSpaces)
          case None => ctx.response().end()
      })
      .onFailure(thr => exceptionHandler.stream()
        .filter(eh => eh.predicate(thr))
        .findFirst()
        .ifPresent(eh => eh.callback(ctx,thr))
    )

object Controller:
  def simple[A:io.circe.Decoder,B:io.circe.Encoder](requestLine: RequestLine,callback:A => B): Controller[A, B] =
    apply(Seq(requestLine),request => async {
      val body = await {request.body}
      val response = callback(body)
      HttpResponse(body = response)
    })