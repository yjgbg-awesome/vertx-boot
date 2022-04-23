package com.github.yjgbg.vertx.boot
package core

import com.typesafe.scalalogging.Logger
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.vertx.core.{Vertx, VertxOptions}
import io.vertx.micrometer.MicrometerMetricsOptions
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Primary}

import java.util.Optional

lazy val log: Logger = Logger[CoreConfig]

trait CoreConfig:
  @ConfigurationProperties("vertx-boot.core.vertx-options.metrics-options")
  @ConditionalOnProperty(name = Array("vertx-boot.core.vertx-options.metrics-options.enabled"), havingValue = "true")
  @Bean def metricsOptions(meterRegistryList: java.util.List[MeterRegistry]): MicrometerMetricsOptions = {
    val compositeMeterRegistry = CompositeMeterRegistry()
    meterRegistryList.forEach(compositeMeterRegistry.add(_))
    new MicrometerMetricsOptions().setMicrometerRegistry(compositeMeterRegistry)
  }
  @ConditionalOnProperty(name = Array("vertx-boot.core.vertx-options.metrics-options.enabled"), havingValue = "true")
  @Bean @Primary def simpleMeterRegistry:MeterRegistry = new SimpleMeterRegistry()

  @ConfigurationProperties("vertx-boot.core.vertx-options")
  @Bean def vertxOptions(metricsOptions: Optional[MicrometerMetricsOptions]): VertxOptions =
    if metricsOptions.isEmpty then VertxOptions() else VertxOptions().setMetricsOptions(metricsOptions.get())

  @Bean def vertx(vertxOptions: VertxOptions): Vertx = {
    log.debug(s"start vertx with vertxOptions:$vertxOptions")
    Vertx.vertx(vertxOptions)
  }

  @Bean def deployAllVerticleBean(vertx: Vertx, verticleBean: java.util.List[VerticleBean[_]]): InitializingBean =
    () => verticleBean.forEach(vb => {
      log.debug(s"deploy verticleBean $verticleBean")
      vertx.deployVerticle(() => vb.verticle, vb.deploymentOptions)
    })