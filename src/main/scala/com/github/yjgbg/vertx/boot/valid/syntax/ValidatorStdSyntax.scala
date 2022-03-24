package com.github.yjgbg.vertx.boot
package valid
package syntax

import kernel.*

import java.util.regex.Pattern

object ValidatorStdSyntax extends ValidatorStdSyntax

trait ValidatorStdSyntax:

  import ValidatorCoreSyntax.and
  import cats.syntax.all.*
  import cps.async
  import vertxCps.CpsSyntax.given

  extension[A] (it: Validator[A])
    inline def notNull[B](inline getter: A => B, errorMsg: ErrorMsg[B]): Validator[A] =
      it.and(getter, errorMsg, it => async(it != null))
    inline def eq[B: cats.Eq](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it === value))
    inline def ne[B: cats.Eq](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it =!= value))
    inline def gt[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it.compare(value) > 0))
    inline def ge[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it.compare(value) >= 0))
    inline def lt[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it.compare(value) < 0))
    inline def le[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B): Validator[A] =
      it.and(getter, errorMsg, it => async(it.compare(value) <= 0))
    inline def between[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], lowerBound: B, upperBound: B): Validator[A] =
      it.ge(getter, errorMsg, lowerBound).le(getter, errorMsg, upperBound)
    inline def notBlank[B <: CharSequence](inline getter: A => B, errorMsg: ErrorMsg[B], allowNull: Boolean): Validator[A] =
      it.and(getter, errorMsg, b => async(if (b == null) allowNull else b.length() > 0))
    inline def notEmpty[B <: java.util.Collection[_]](inline getter: A => B, errorMsg: ErrorMsg[B]): Validator[A] =
      it.and(getter, errorMsg, b => async(b != null && b.size =!= 0))
    inline def regexp[B <: CharSequence](inline getter: A => B, errorMsg: ErrorMsg[B], allowNull: Boolean, pattern: String): Validator[A] =
      it.and(getter, errorMsg, it => async(if (it == null) allowNull else Pattern.matches(pattern, it)))
    inline def in[B: cats.Eq](inline getter: A => B, errorMsg: ErrorMsg[B], values: Seq[B]): Validator[A] =
      it.and(getter, errorMsg, it => async (values.find(b => it === b).isDefined))