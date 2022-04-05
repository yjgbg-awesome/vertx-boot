package com.github.yjgbg.vertx.boot
package valid

import io.vertx.ext.web.RoutingContext
import org.springframework.context.annotation.Bean
import syntax.AllSyntax.*

// int.maxValue为最低优先级
val log = com.typesafe.scalalogging.Logger[ValidationConfig]
trait ValidationConfig(priority: Int):
  self: http.server.HttpServerConfig & core.CoreConfig =>
  @Bean def validateResultHandler: http.server.ExceptionHandler[kernel.Result] =
    http.server.ExceptionHandler[kernel.Result](
      order = priority,
      predicate = _.isInstanceOf[kernel.Result],
      callback = (ctx: RoutingContext, result: kernel.Result) => {
        val encoder = io.circe.Encoder.encodeMap[String, Set[String]]
        ctx.response().setStatusCode(422).end(encoder(result.toMessageMap).noSpaces)
      }
    )
  @Bean def throwableHandler: http.server.ExceptionHandler[Throwable] = http.server.ExceptionHandler[Throwable](
    order = Int.MaxValue,
    callback = (ctx:RoutingContext,thr: Throwable) => {
      ctx.response().setStatusCode(500).end(if(thr.getMessage!= null) thr.getMessage else thr.toString)
      log.error("an unexpected error happened",thr)
    }
  )