package com.github.yjgbg.vertx.boot

object api extends api
trait api:
  type Logger = com.typesafe.scalalogging.Logger
  type CoreConfig = core.CoreConfig
  type VerticleBean[State] = core.VerticleBean[State]
  type HttpClientConfig = http.client.HttpClientConfig
  type HttpServerConfig = http.server.HttpServerConfig
  type Controller[A, B] = http.server.Controller[A, B]
  type Middleware = http.server.Middleware
  type ExceptionHandler[A <: Throwable] = http.server.ExceptionHandler[A]
  type HttpRequest[A] = http.server.HttpRequest[A]
  type HttpResponse[A] = http.server.HttpResponse[A]
  type Method = http.server.Method
  type RequestLine = http.server.RequestLine
  type HttpStatus = http.server.HttpStatus
  type ResponseHeaders = http.server.ResponseHeaders
  type MySQLConfig = sql.MySQLConfig
  type RedisConfig = redis.RedisConfig
  type Validator[A] = valid.kernel.Validator[A]
  type Result = valid.kernel.Result
  val Logger: com.typesafe.scalalogging.Logger.type = com.typesafe.scalalogging.Logger
  val VerticleBean: core.VerticleBean.type = core.VerticleBean
  val Controller: http.server.Controller.type = http.server.Controller
  val Middleware: http.server.Middleware.type = http.server.Middleware
  val ExceptionHandler:http.server.ExceptionHandler.type =http.server.ExceptionHandler
  val HttpResponse: http.server.HttpResponse.type = http.server.HttpResponse
  val Validator: valid.kernel.Validator.type = valid.kernel.Validator
  val Result: valid.kernel.Result.type = valid.kernel.Result
