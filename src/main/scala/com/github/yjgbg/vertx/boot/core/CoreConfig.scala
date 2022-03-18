package com.github.yjgbg.vertx.boot
package core

import io.vertx.core.{Vertx, VertxOptions}
import com.typesafe.scalalogging.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

lazy val log: Logger = Logger[CoreConfig]
trait CoreConfig:
  @ConfigurationProperties("vertx-boot.core.vertx-options")
  @Bean def vertxOptions = new VertxOptions()

  @Bean def vertx(vertxOptions: VertxOptions): Vertx = {
    log.debug(s"start vertx with vertxOptions:$vertxOptions")
    Vertx.vertx(vertxOptions)
  }

  @Bean def deployAllVerticleBean(vertx: Vertx, verticleBean: java.util.List[VerticleBean]): InitializingBean =
    () => verticleBean.forEach(vb => {
      log.debug(s"deploy verticleBean $verticleBean")
      vertx.deployVerticle(() => vb.verticle, vb.deploymentOptions)
    })