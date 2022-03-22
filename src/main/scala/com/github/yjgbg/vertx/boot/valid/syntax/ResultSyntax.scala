package com.github.yjgbg.vertx.boot.valid
package syntax

import com.github.yjgbg.vertx.boot.valid.core.Result

trait ResultSyntax:
  private val SELF = "__self__"

  import cats.syntax.all.catsSyntaxSemigroup

  extension (r: core.Result)
    def toMessageMap: Map[String, Set[String]] = r match
      case core.Result.None => Map()
      case core.Result.Simple(_, msg) => Map(SELF -> Set(msg))
      case core.Result.Plus(a, b) => a.toMessageMap |+| b.toMessageMap
      case core.Result.Transformation(field, fieldResult: core.Result) => fieldResult.toMessageMap.map {
        case (SELF, v) => (field, v)
        case (k, v) => (s"$field.$k", v)
      }
    def hasError: Boolean = r != core.Result.None

    def mapMessage(func: String => String): core.Result = r match
      case core.Result.None => Result.None
      case core.Result.Simple(ro, msg) => core.Result.Simple(ro, func(msg))
      case core.Result.Plus(r0, r1) => core.Result.Plus(r0.mapMessage(func), r1.mapMessage(func))
      case core.Result.Transformation(field, error) => core.Result.Transformation(field, error.mapMessage(func))

object ResultSyntax extends ResultSyntax
