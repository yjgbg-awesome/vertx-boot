package com.github.yjgbg.vertx.boot
package core

import com.typesafe.scalalogging.Logger
import io.vertx.core.{Vertx, VertxOptions}
import io.vertx.micrometer.MicrometerMetricsOptions
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

import java.util.Optional

lazy val log: Logger = Logger[CoreConfig]

trait CoreConfig:
  @ConfigurationProperties("vertx-boot.core.vertx-options.metrics-options")
  @ConditionalOnProperty(name = Array("vertx-boot.core.vertx-options.metrics-options.enabled"), havingValue = "true")
  @Bean def metricsOptions = new MicrometerMetricsOptions()

  @ConfigurationProperties("vertx-boot.core.vertx-options")
  @Bean def vertxOptions(metricsOptions: Optional[MicrometerMetricsOptions]): VertxOptions =
    if (metricsOptions.isEmpty) new VertxOptions() else new VertxOptions().setMetricsOptions(metricsOptions.get())

  @Bean def vertx(vertxOptions: VertxOptions): Vertx = {
    log.debug(s"start vertx with vertxOptions:$vertxOptions")
    Vertx.vertx(vertxOptions)
  }

  @Bean def deployAllVerticleBean(vertx: Vertx, verticleBean: java.util.List[VerticleBean]): InitializingBean =
    () => verticleBean.forEach(vb => {
      log.debug(s"deploy verticleBean $verticleBean")
      vertx.deployVerticle(() => vb.verticle, vb.deploymentOptions)
    })