package com.github.yjgbg.vertx.boot.sql

import io.vertx.core.Future

object SQLSyntax extends SQLSyntax

trait SQLSyntax:
  extension (sql: String)
    def exe[A: io.circe.Decoder](using typedSQLClient: TypedSQLClient): Future[Seq[A]] =
      typedSQLClient.execute(sql)
    def exe[A: io.circe.Decoder](args: Seq[Any])(using typedSQLClient: TypedSQLClient): Future[Seq[A]] =
      typedSQLClient.preparedExecute(sql,args)