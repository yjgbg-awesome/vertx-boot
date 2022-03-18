package com.github.yjgbg.vertx.boot.http.server

import io.vertx.core.{Future, MultiMap}
import io.vertx.core.http.Cookie
import io.vertx.ext.web.RoutingContext

case class HttpRequest[A: io.circe.Decoder](routingContext: RoutingContext):
  def params: MultiMap = routingContext.request().params()
  def headers: MultiMap = routingContext.request().headers()
  def cookies: java.util.Set[Cookie] = routingContext.request().cookies()
  import io.circe.parser.decode
  def body: Future[A] = routingContext.request().body()
    .map(buffer => buffer.toString).asInstanceOf[Future[String]]
    .flatMap(string => decode[A](string) match {
      case Left(value) => Future.failedFuture(value)
      case Right(value) => Future.succeededFuture(value)
    })

