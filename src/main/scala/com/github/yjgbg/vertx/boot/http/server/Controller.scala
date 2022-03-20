package com.github.yjgbg.vertx.boot
package http.server

import io.vertx.core.{Future, Handler}
import io.vertx.ext.web.RoutingContext

case class Controller[A: io.circe.Decoder, B: io.circe.Encoder]
(requestLine: Seq[RequestLine], callback: HttpRequest[A] => Future[HttpResponse[B]]):
  def toHandler(exceptionHandler: java.util.List[ExceptionHandler[Throwable]]): Handler[RoutingContext] =
    ctx => callback(HttpRequest(ctx)).map(_.use(ctx))
      .onFailure(thr => exceptionHandler.stream()
        .filter(_.predicate(thr))
        .findFirst()
        .ifPresent(_.callback(ctx,thr))
      )