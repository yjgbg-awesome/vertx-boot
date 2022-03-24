package com.github.yjgbg.vertx.boot.valid
package syntax

import com.github.yjgbg.vertx.boot.valid.kernel.Result

trait ResultSyntax:
  private val SELF = "__self__"

  import cats.syntax.all.catsSyntaxSemigroup

  extension (r: Result)
    def toMessageMap: Map[String, Set[String]] = r match
      case Result.None => Map()
      case Result.Simple(_, msg) => Map(SELF -> Set(msg))
      case Result.Plus(a, b) => a.toMessageMap |+| b.toMessageMap
      case Result.Transformation(field, fieldResult: Result) => fieldResult.toMessageMap.map {
        case (SELF, v) => (field, v)
        case (k, v) => (s"$field.$k", v)
      }
    def hasError: Boolean = r != Result.None

    def mapMessage(func: String => String): Result = r match
      case Result.None => Result.None
      case Result.Simple(ro, msg) => Result.Simple(ro, func(msg))
      case Result.Plus(r0, r1) => Result.Plus(r0.mapMessage(func), r1.mapMessage(func))
      case Result.Transformation(field, error) => Result.Transformation(field, error.mapMessage(func))

object ResultSyntax extends ResultSyntax
