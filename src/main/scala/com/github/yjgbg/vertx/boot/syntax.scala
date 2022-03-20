package com.github.yjgbg.vertx.boot

object syntax:
  object AllSyntax extends RedisSyntax,CpsSyntax,ValidSyntax
  type RedisSyntax = redis.RedisSyntax
  type CpsSyntax = vertxCps.CpsSyntax
  type ValidSyntax = valid.ValidSyntax
  val RedisSyntax: redis.RedisSyntax.type = redis.RedisSyntax
  val CpsSyntax: vertxCps.CpsSyntax.type = vertxCps.CpsSyntax
  val ValidSyntax: valid.ValidSyntax.type = valid.ValidSyntax