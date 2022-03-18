package com.github.yjgbg.vertx.boot

package object valid:

  import valid.Macros
  import valid.core.*

  trait CoreSyntax extends ResultSyntax, ValidatorSyntax,ValidatorExtSyntax

  object CoreSyntax extends CoreSyntax

  object ResultSyntax extends ResultSyntax

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

  object ValidatorSyntax extends ValidatorSyntax

  trait ValidatorSyntax:
    inline def inlined[A, B](inline getter: A => B): Getter[A, B] = (Macros.propName(getter), getter)

    extension[A] (it: Validator[A])
      def +[B](validator: Validator[B]): Validator[A & B] = Validator.plus(it, validator)
      def `for`[B <: A]: Validator[B] = it
      inline def and[B](inline getter: A => B, validator: Validator[B]): Validator[A] =
        Validator.plus(it, Validator.transform(inlined(getter), validator))
      inline def and[B](inline getter: A => B, errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
        Validator.plus(it, Validator.transform(inlined(getter), Validator.simple(errorMsg, constraint)))
      // 将A类型的校验器转换为A容器的校验器
      def iter: Validator[Iterable[A]] = Validator.func {
        case null => Validator.none
        case iterable => iterable.zipWithIndex
          .map(tuple => Validator.transform[Iterable[A], A]((tuple._2.toString, _ => tuple._1), it))
          .foldLeft(Validator.none)(Validator.plus)
      }
      inline def andIter[B](inline getter: A => Iterable[B], errorMsg: ErrorMsg[B], constraint: Constraint[B]): Validator[A] =
        Validator.plus(it, Validator.transform(inlined(getter), Validator.simple(errorMsg, constraint).iter))
    extension[A] (it: FailFast => A)
      def failFast: A = it(true)
      def nonFailFast: A = it(false)

  object ValidatorExtSyntax extends ValidatorExtSyntax

  trait ValidatorExtSyntax:

    import ValidatorSyntax.and
    import cats.syntax.all.*
    import cps.async
    import api.given

    extension[A] (it: Validator[A])
      inline def notNull[B](inline getter: A => B, errorMsg: ErrorMsg[B]): Validator[A] =
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