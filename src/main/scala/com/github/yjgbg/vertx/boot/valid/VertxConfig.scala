package com.github.yjgbg.vertx.boot
package valid

import io.vertx.ext.web.RoutingContext
import org.springframework.context.annotation.Bean
import syntax.AllSyntax.*

case class ResultHasError(result: kernel.Result) extends RuntimeException(null,null,false,false)
// int.maxValue为最低优先级
trait ValidConfig(priority: Int):
  self: http.server.HttpServerConfig & core.CoreConfig =>
  @Bean def resultHasErrorHandler: http.server.ExceptionHandler[ResultHasError] =
    http.server.ExceptionHandler[ResultHasError](
      order = priority,
      callback = (ctx: RoutingContext, thr: ResultHasError) => {
        val encoder = io.circe.Encoder.encodeMap[String, Set[String]]
        ctx.response().setStatusCode(422).end(encoder(thr.result.toMessageMap).noSpaces)
      }
    )