package com.github.yjgbg.vertx.boot.core

import io.vertx.core.{AbstractVerticle, DeploymentOptions, Future, Promise}

import java.util.concurrent.atomic.AtomicReference

case class VerticleBean[State](
                                deploymentOptions:DeploymentOptions = new DeploymentOptions(),
                                onStart:() => Future[State],
                                onStop:State => Future[Unit] = (ignored:State) => Future.succeededFuture()
                       ):
  def verticle: AbstractVerticle = new AbstractVerticle:
    val state = new AtomicReference[State]()
    override def start(startPromise: Promise[Void]): Unit = onStart.apply()
      .onSuccess(it => {
        state.set(it)
        startPromise.complete()
      })
      .onFailure(startPromise.fail(_))
    override def stop(stopPromise: Promise[Void]): Unit = onStop(state.get())
      .onSuccess(unit => stopPromise.complete())
      .onFailure(stopPromise.fail(_))