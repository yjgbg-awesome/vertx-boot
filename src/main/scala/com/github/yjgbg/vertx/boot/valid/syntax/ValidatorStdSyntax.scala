package com.github.yjgbg.vertx.boot
package valid
package syntax

import core.*
import java.util.regex.Pattern
object ValidatorStdSyntax extends ValidatorStdSyntax

trait ValidatorStdSyntax:
  import ValidatorCoreSyntax.and
  import cats.syntax.all.*
  import cps.async
  import vertxCps.CpsSyntax.given_VertxCpsMonad

  extension[A] (it: Validator[A])
    inline def notNull[B](inline getter: A => B, errorMsg: ErrorMsg[B]) =
      it.and(getter, errorMsg, it => async(it != null))
    inline def eq[B: cats.Eq](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it === value))
    inline def ne[B: cats.Eq](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it =!= value))
    inline def gt[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it.compare(value) > 0))
    inline def ge[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it.compare(value) >= 0))
    inline def lt[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it.compare(value) < 0))
    inline def le[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], value: B) =
      it.and(getter, errorMsg, it => async(it.compare(value) <= 0))
    inline def between[B: cats.Order](inline getter: A => B, errorMsg: ErrorMsg[B], lowerBound: B, upperBound: B) =
      it.ge(getter, errorMsg, lowerBound).le(getter, errorMsg, upperBound)
    inline def notBlank[B <: CharSequence](inline getter: A => B, errorMsg: ErrorMsg[B], allowNull: Boolean) =
      it.and(getter, errorMsg, b => async(if (b == null) allowNull else b.length() > 0))
    inline def notEmpty[B <: java.util.Collection[_]](inline getter: A => B, errorMsg: ErrorMsg[B]) =
      it.and(getter, errorMsg, b => async(b != null && b.size =!= 0))
    inline def regexp[B <: CharSequence](inline getter:A => B,errorMsg: ErrorMsg[B],allowNull:Boolean,pattern: String) =
      it.and(getter,errorMsg,value => async(if(value == null) allowNull else Pattern.matches(pattern,value)))