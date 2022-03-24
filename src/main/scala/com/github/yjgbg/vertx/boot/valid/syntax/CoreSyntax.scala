package com.github.yjgbg.vertx.boot
package valid
package syntax

import com.github.yjgbg.vertx.boot.valid.kernel.*

trait CoreSyntax:
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
      case Result.Transformation(field, fieldResult) => Result.Transformation(field, fieldResult.mapMessage(func))

  inline def getter[A, B](inline getter: A => B): Getter[A, B] = (Macros.propName(getter), getter)

  extension[A] (it: Validator[A])
    def plus[B](validator: Validator[B]): Validator[A & B] = Validator.plus(it, validator)
    def plus(errorMsg: ErrorMsg[A], constraint: Constraint[A]): Validator[A] =
      it.plus(Validator.simple(errorMsg, constraint))
    inline def and[B](inline prop: A => B, validator: Validator[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), validator))
    inline def and[B](inline prop: A => B, errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), Validator.simple(errorMsg, constraint)))
    // 将A类型的校验器转换为A容器的校验器
    def iterable[CC[_] <: Iterable[_]]: Validator[CC[A]] = Validator.func {
      case null => Validator.none
      case iterable => iterable.zipWithIndex
        .map { case (value:A, i) => Validator.transform[CC[A], A]((i.toString, _ => value), it) }
        .foldLeft(Validator.none)(Validator.plus)
    }
    inline def andIter[B](inline prop: A => Iterable[B], errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), Validator.simple(errorMsg, constraint).iterable))

object CoreSyntax extends CoreSyntax