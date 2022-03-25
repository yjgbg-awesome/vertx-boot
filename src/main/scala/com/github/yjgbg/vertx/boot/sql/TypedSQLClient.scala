package com.github.yjgbg.vertx.boot
package sql

import io.vertx.core.Future

import scala.jdk.CollectionConverters.*
import io.circe.parser.decode
import io.vertx.sqlclient.{Row, RowSet}

case class TypedSQLClient(private val sqlClient: io.vertx.sqlclient.SqlClient):
  import vertxCps.CpsSyntax.given
  import cps.{async, await}

  def preparedExecute[A: io.circe.Decoder](sql: String, args:Seq[Any]): Future[Seq[A]] = async {
    val query = sqlClient.preparedQuery(sql)
    val tuple = io.vertx.sqlclient.Tuple.from(args.toArray)
    val rowIteratorJava = await(query.execute(tuple))
    rowIteratorJava.asScala.map { row =>
      decode[A](row.toJson.toString) match
        case Right(a) => a
        case Left(error) => throw error
    }.toSeq
  }

  def execute[A: io.circe.Decoder](sql: String): Future[Seq[A]] = async {
    val query = sqlClient.query(sql)
    val f :Future[RowSet[Row]]= query.execute()
    val rowIteratorJava = await(f)
    rowIteratorJava.asScala.map { row =>
      decode[A](row.toJson.toString) match
        case Right(a) => a
        case Left(error) => throw error
    }.toSeq
  }
