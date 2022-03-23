package com.github.yjgbg.vertx.boot.valid

import com.github.yjgbg.vertx.boot.syntax.CpsSyntax.given
import cps.{async, await}
import io.vertx.core.Future

import java.util

object core:
  object Result:
    case object None

    case class Simple(rejectObject: Any, msg: String)

    case class Plus(error0: Result, error1: Result)

    object Plus:
      def apply(r0: Result, r1: Result): Result = if (r0 == None) r1 else if (r1 == None) r0 else new Plus(r0, r1)

    case class Transformation(field: String, error: Result)

    object Transformation:
      def apply(field: String, r: Result): None.type | Transformation =
        if (r == None) None else new Transformation(field, r)

  type Result = Result.None.type | Result.Simple | Result.Plus | Result.Transformation

  // 证明 Result 为幺半群Monoid
  given cats.kernel.Monoid[Result] = cats.kernel.Monoid.instance(Result.None, Result.Plus(_, _))

  type ErrorMsg[-A] = (A => String) | String
  type FailFast = Boolean
  type Parallel = Boolean
  type Constraint[-A] = A => Future[Boolean]
  type Getter[-A, +B] = (String, A => B)
  type Validator[-A] = FailFast => Parallel => A => Future[Result]

  object Validator:
    // 空校验器
    def none[A]: Validator[A] = _ => _ => _ => Future.succeededFuture(Result.None)

    def apply[A]: Validator[A] = none

    // 简单校验器
    def simple[A](errorMsg: ErrorMsg[A], constraint: Constraint[A]): Validator[A] =
      failFast => parallel => a => async {
        if (await(constraint(a))) Result.None else errorMsg match
          case string: String => Result.Simple(a, string)
          case func: (A => String) => Result.Simple(a, func(a))
      }

    // 一类复杂校验器： 由两个校验器构造而成
    def plus[A, B](v0: Validator[A], v1: Validator[B]): Validator[A & B] = failFast => parallel => obj => async {
      if (parallel) {
        val f0 = v0(failFast)(true)(obj)
        val f1 = v1(failFast)(true)(obj)
        val r0: Result = await {
          f0
        }
        if (failFast && r0 != Result.None) r0 else {
          val r1: Result = await(f1)
          Result.Plus(r0, r1)
        }
      } else {
        val r0: Result = await {
          v0(failFast)(false)(obj)
        }
        if (failFast && r0 != Result.None) r0 else Result.Plus(r0, await {
          v1(failFast)(false)(obj)
        })
      }
    }

    // 二类复杂校验器： 由一个属性提取器和一个校验器构造而成
    def transform[A, B](getter: Getter[A, B], validator: Validator[B]): Validator[A] =
      failFast => parallel => a => async {
        Result.Transformation(getter._1, await {
          validator(failFast)(parallel)(getter._2(a))
        })
      }

    // 三类复杂校验器: 由一个目标对象到校验器的function构造而成
    def func[A](f: A => Validator[A]): Validator[A] = failFast => parallel => a => f(a)(failFast)(parallel)(a)

  // 证明 Validator 为幺半群Monoid
  given[A]: cats.kernel.Monoid[Validator[A]] = cats.kernel.Monoid.instance(Validator.none, Validator.plus(_, _))