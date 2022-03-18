package com.github.yjgbg.vertx.boot.redis

import io.vertx.core.Future
import io.vertx.redis.client.{Command, Redis, Request, Response}

trait RedisSyntax:
  import io.vertx.core.buffer.Buffer
  extension (command:Command)
    def arg(arg:Int|Byte|Long|Float|Short|Buffer|Double|String|Boolean):Request = arg match {
      case int:Int => Request.cmd(command).arg(int)
      case byte:Byte => Request.cmd(command).arg(byte)
      case long:Long => Request.cmd(command).arg(long)
      case float:Float => Request.cmd(command).arg(float)
      case short:Short => Request.cmd(command).arg(short)
      case buffer:Buffer => Request.cmd(command).arg(buffer)
      case double:Double => Request.cmd(command).arg(double)
      case string:String => Request.cmd(command).arg(string)
      case boolean:Boolean => Request.cmd(command).arg(boolean)
    }

  extension (request:Request)
    def send(using redis:Redis): Future[Response] = redis.send(request)
  import scala.jdk.CollectionConverters.*
  extension (iterable:Iterable[Request])
    def batchSend(using redis:Redis): Future[Seq[Response]] = redis.batch(iterable.toSeq.asJava)
      .map(_.asScala.toSeq)
  extension (iterable:java.util.Collection[Request])
    def send(using redis:Redis):Future[Seq[Response]] = redis.batch(iterable match {
      case list:java.util.List[Request] => list
      case _:java.util.Collection[Request] => iterable.stream.toList
    }).map(_.asScala.toSeq)