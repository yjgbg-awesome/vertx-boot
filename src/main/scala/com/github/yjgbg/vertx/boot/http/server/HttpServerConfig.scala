package com.github.yjgbg.vertx.boot
package http.server

import com.github.yjgbg.vertx.boot.valid.{kernel, syntax}
import com.typesafe.scalalogging.Logger
import io.vertx.core.http.{HttpMethod, HttpServer, HttpServerOptions}
import io.vertx.core.{DeploymentOptions, Vertx, VertxOptions}
import io.vertx.ext.web.{Router, RoutingContext}
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

import java.util.concurrent.atomic.AtomicReference

lazy val log: Logger = Logger[HttpServerConfig]

trait HttpServerConfig:
  self: core.CoreConfig =>

  @ConfigurationProperties("vertx-boot.http.server.deployment-options")
  @Bean def httpServerDeploymentOptions(vertxOptions: VertxOptions): DeploymentOptions =
    new DeploymentOptions().setInstances(vertxOptions.getEventLoopPoolSize)

  @ConfigurationProperties("vertx-boot.http.server.options")
  @Bean def httpServerOptions = new HttpServerOptions()

  // 用controller，middleware，以及exceptionHandler部署一个http服务器
  @Bean def httpServerVerticleBean(vertx: Vertx,
                                   httpOptions: HttpServerOptions,
                                   deploymentOptions: DeploymentOptions,
                                   middlewareList: java.util.List[Middleware],
                                   controllerList: java.util.List[Controller[_, _]],
                                   exceptionHandlerList: java.util.List[ExceptionHandler[_]]
                                  ): core.VerticleBean[HttpServer] = core.VerticleBean(
    deploymentOptions,
    () => {
      val router = Router.router(vertx)
      middlewareList.forEach(mid => {
        log.info(s"add middleware: ${mid.toString}")
        mid.requestLine.foreach((method, path) => router
            .route(HttpMethod.valueOf(method), path)
            .handler(mid.callback(_))
        )
      })
      controllerList.forEach { ctl => {
        log.info(s"add controller: ${ctl.toString}")
        val handler = ctl.toHandler(exceptionHandlerList.asInstanceOf)
        ctl.requestLine.foreach((method, path) => router
          .route(HttpMethod.valueOf(method), path)
          .handler(ctx => handler.handle(ctx))
        )
      }}
      vertx.createHttpServer(httpOptions).requestHandler(router).listen()
        .onSuccess(_ =>
          log.info(s"http server started on host = ${httpOptions.getHost},port = ${httpOptions.getPort}"))
        .onFailure(log.error("http server start failed", _))
    },
    httpServer => httpServer.close()
      .onSuccess(_ => log.info("http server closed"))
      .map(_ => ()))
  // 排序在0的异常处理器：校验结果处理器
  @Bean def validateResultHandler: http.server.ExceptionHandler[kernel.Result] =
    http.server.ExceptionHandler[kernel.Result](
      order = Int.MaxValue - 1,
      predicate = _.isInstanceOf[kernel.Result],
      callback = (ctx: RoutingContext, result: kernel.Result) => {
        val encoder = io.circe.Encoder.encodeMap[String, Set[String]]
        import valid.syntax.CoreSyntax.toMessageMap
        ctx.response().setStatusCode(422).end(encoder(result.toMessageMap).noSpaces)
      }
    )
  // 排在最后的异常处理器: 通用异常处理器
  @Bean def throwableHandler: http.server.ExceptionHandler[Throwable] = http.server.ExceptionHandler[Throwable](
    order = Int.MaxValue,
    callback = (ctx:RoutingContext,thr: Throwable) => {
      ctx.response().setStatusCode(500).end(if(thr.getMessage!= null) thr.getMessage else thr.toString)
      log.error("an unexpected error happened",thr)
    }
  )