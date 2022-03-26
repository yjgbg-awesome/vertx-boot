package com.github.yjgbg.vertx.boot
package http.server

import com.typesafe.scalalogging.Logger
import io.vertx.core.http.{HttpMethod, HttpServer, HttpServerOptions}
import io.vertx.core.{DeploymentOptions, Vertx, VertxOptions}
import io.vertx.ext.web.Router
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
        mid.requestLine.foreach {
          case (method, path) => router
            .route(HttpMethod.valueOf(method), path)
            .handler(mid.callback(_))
        }
      })
      controllerList.forEach { ctl => {
        log.info(s"add controller: ${ctl.toString}")
        val handler = ctl.toHandler(exceptionHandlerList.asInstanceOf)
        ctl.requestLine.foreach {
          case (method, path) => router.route(HttpMethod.valueOf(method), path)
            .handler(ctx => handler.handle(ctx))
        }
      }}
      vertx.createHttpServer(httpOptions).requestHandler(router).listen()
        .onSuccess(_ =>
          log.info(s"http server started on host = ${httpOptions.getHost},port = ${httpOptions.getPort}"))
        .onFailure(log.error("http server start failed", _))
    },
    httpServer => httpServer.close()
      .onSuccess(_ => log.info("http server closed"))
      .map(_ => ()))