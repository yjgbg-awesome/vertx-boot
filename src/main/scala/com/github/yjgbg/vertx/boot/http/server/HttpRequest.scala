package com.github.yjgbg.vertx.boot.http.server

import io.vertx.core.{Future, MultiMap}
import io.vertx.core.http.Cookie
import io.vertx.ext.web.RoutingContext

case class HttpRequest[A: io.circe.Decoder](routingContext: RoutingContext):
  def params: MultiMap = routingContext.request().params()
  def headers: MultiMap = routingContext.request().headers()
  def cookies: java.util.Set[Cookie] = routingContext.request().cookies()
  import io.circe.jawn.decodeByteBuffer
  def body: Future[A] = routingContext.request().body()
    .map(buf => decodeByteBuffer(buf.getByteBuf.nioBuffer) match
      case Left(throwable) => throw throwable
      case Right(value) =>value
    )

