package com.github.yjgbg.vertx.boot.core

import io.vertx.core.{AbstractVerticle, DeploymentOptions, Promise}

case class VerticleBean(
                         deploymentOptions:DeploymentOptions = new DeploymentOptions(),
                         onStart:Promise[Unit] => Unit = it => it.complete(),
                         onStop:Promise[Unit] => Unit = it => it.complete()
                       ):
  def verticle: AbstractVerticle = new AbstractVerticle:
    override def start(startPromise: Promise[Void]): Unit = onStart(startPromise.asInstanceOf)
    override def stop(stopPromise: Promise[Void]): Unit = onStop(stopPromise.asInstanceOf)