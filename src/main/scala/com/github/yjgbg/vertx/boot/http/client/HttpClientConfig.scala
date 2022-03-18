package com.github.yjgbg.vertx.boot
package http.client

import io.vertx.core.Vertx
import io.vertx.core.http.{HttpClient, HttpClientOptions}
import io.vertx.ext.web.client.{WebClient, WebClientOptions}
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

trait HttpClientConfig:
  self:core.CoreConfig =>
  @ConfigurationProperties("vertx-boot.http.client.options")
  @Bean def httpClientOptions:WebClientOptions = new WebClientOptions()
  @Bean def httpClient(vertx:Vertx,httpClientConfig: HttpClientConfig): WebClient =
    WebClient.create(vertx,httpClientOptions)