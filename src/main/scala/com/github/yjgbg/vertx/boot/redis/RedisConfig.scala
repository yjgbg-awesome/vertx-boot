package com.github.yjgbg.vertx.boot
package redis

import io.vertx.core.{DeploymentOptions, Vertx}
import io.vertx.redis.client.{Redis, RedisAPI, RedisOptions}
import org.springframework.context.annotation.Bean
import org.springframework.boot.context.properties.ConfigurationProperties

trait RedisConfig:
  self:core.CoreConfig =>
  @ConfigurationProperties("vertx-boot.redis.options")
  @Bean def redisOptions:RedisOptions = new RedisOptions()
  @Bean def redis(vertx:Vertx,redisOptions: RedisOptions):Redis = Redis.createClient(vertx,redisOptions)
  @Bean def redisAPI(redis: Redis):RedisAPI = RedisAPI.api(redis)