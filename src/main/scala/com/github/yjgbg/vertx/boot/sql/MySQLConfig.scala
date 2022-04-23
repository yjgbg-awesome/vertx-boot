package com.github.yjgbg.vertx.boot.sql

import com.github.yjgbg.vertx.boot.core
import io.vertx.core.Vertx
import io.vertx.mysqlclient.{MySQLConnectOptions, MySQLPool}
import io.vertx.sqlclient.PoolOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

trait MySQLConfig:
  self: core.CoreConfig =>
  @ConfigurationProperties("vertx-boot.mysql.connection-options")
  @Bean def mySQLConnectOptions = new MySQLConnectOptions()

  @ConfigurationProperties("vertx-boot.mysql.pool-options")
  @Bean def poolOptions = new PoolOptions()

  @Bean def mySQLPool(vertx: Vertx, mySQLConnectOptions: MySQLConnectOptions, poolOptions: PoolOptions): MySQLPool =
    MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions)
