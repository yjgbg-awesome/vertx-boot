package com.github.yjgbg.vertx.boot
package valid
package syntax

import core.{Constraint, ErrorMsg, FailFast, Getter, Validator}

trait ValidatorCoreSyntax:
  inline def getter[A, B](inline getter: A => B): Getter[A, B] = (Macros.propName(getter), getter)
  extension[A] (it: Validator[A])
    def plus[B](validator: Validator[B]): Validator[A & B] = Validator.plus(it, validator)
    def plus(errorMsg:ErrorMsg[A],constraint: Constraint[A]):Validator[A] =
      it.plus(Validator.simple(errorMsg,constraint))
    inline def and[B](inline prop: A => B, validator: Validator[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), validator))
    inline def and[B](inline prop: A => B, errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), Validator.simple(errorMsg, constraint)))
    // 将A类型的校验器转换为A容器的校验器
    def iter[B<: Iterable[A]]: Validator[B] = Validator.func {
      case null => Validator.none
      case iterable => iterable.zipWithIndex
        .map(tuple => Validator.transform[Iterable[A], A]((tuple._2.toString, _ => tuple._1), it))
        .foldLeft(Validator.none)(Validator.plus)
    }
    inline def andIter[B](inline prop: A => Iterable[B], errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
      Validator.plus(it, Validator.transform(getter(prop), Validator.simple(errorMsg, constraint).iter))
object ValidatorCoreSyntax extends ValidatorCoreSyntax