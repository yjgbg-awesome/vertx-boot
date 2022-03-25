package com.github.yjgbg.vertx.boot

object syntax:
  object all extends RedisSyntax,CpsSyntax,ValidSyntax
  type RedisSyntax = redis.RedisSyntax
  type CpsSyntax = vertxCps.CpsSyntax
  type ValidSyntax = valid.syntax.AllSyntax
  type SQLSyntax = sql.SQLSyntax
  val RedisSyntax: redis.RedisSyntax.type = redis.RedisSyntax
  val CpsSyntax: vertxCps.CpsSyntax.type = vertxCps.CpsSyntax
  val ValidSyntax: valid.syntax.AllSyntax.type = valid.syntax.AllSyntax
  val SQLSyntax:sql.SQLSyntax.type = sql.SQLSyntax
