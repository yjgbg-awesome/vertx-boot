package com.github.yjgbg.vertx.boot.vertxCps

import cps.{CpsMonadInstanceContext, CpsSchedulingMonad}
import io.vertx.core.{Future, Promise}

import scala.util.{Failure, Success, Try}

trait CpsSyntax:
  given CpsSchedulingMonad[io.vertx.core.Future] with CpsMonadInstanceContext[io.vertx.core.Future] with {
    override def pure[A](t: A): Future[A] = Future.succeededFuture(t)

    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f(_))

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f(_))

    override def error[A](e: Throwable): Future[A] = io.vertx.core.Future.failedFuture(e)

    override def mapTry[A, B](fa: Future[A])(f: Try[A] => B): Future[B] = fa.map(Success(_))
      .asInstanceOf[Future[Try[A]]]
      .recover(thr => Future.succeededFuture(Failure(thr)))
      .map(f(_))

    override def flatMapTry[A, B](fa: Future[A])(f: Try[A] => Future[B]): Future[B] = fa.map(Success(_))
      .asInstanceOf[Future[Try[A]]]
      .recover(it => Future.succeededFuture(Failure(it)))
      .flatMap(f(_))

    override def restore[A](fa: Future[A])(fx: Throwable => Future[A]): Future[A] = super.restore(fa)(fx)

    override def adoptCallbackStyle[A](source: (Try[A] => Unit) => Unit): Future[A] = {
      val promise = Promise.promise[Try[A]]()
      source(promise.complete)
      promise.future().flatMap {
        case Success(s) => Future.succeededFuture(s)
        case Failure(exception) => Future.failedFuture(exception)
      }
    }

    override def spawn[A](op: => Future[A]): Future[A] = op

    override def tryCancel[A](op: Future[A]): Future[Unit] =
      Future.failedFuture(new UnsupportedOperationException("VertxFutureAsyncMonad.tryCancel is unsupported"))
  }
object CpsSyntax extends CpsSyntax
  
