package com.github.yjgbg.vertx.boot
package core

import com.typesafe.scalalogging.Logger
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.vertx.core.{Vertx, VertxOptions}
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.core.metrics.MetricsOptions
import io.vertx.micrometer.backends.BackendRegistries
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnProperty}
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, DependsOn, Primary}

import java.util.Optional

lazy val log: Logger = Logger[CoreConfig]

trait CoreConfig:
  @ConfigurationProperties("vertx-boot.core.vertx-options.metrics-options")
  @ConditionalOnProperty(name = Array("vertx-boot.core.vertx-options.metrics-options.enabled"), havingValue = "true")
  @Bean def metricsOptions: MetricsOptions = MicrometerMetricsOptions()

  @DependsOn(Array("vertx"))
  @ConditionalOnBean(Array(classOf[MetricsOptions]))
  @Bean def processingAllMeterBinder(meterBinder: java.util.List[MeterBinder]):InitializingBean = () =>
    meterBinder.forEach(_.bindTo(BackendRegistries.getDefaultNow))

  @ConfigurationProperties("vertx-boot.core.vertx-options")
  @Bean def vertxOptions(metricsOptions: Optional[MetricsOptions]): VertxOptions =
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