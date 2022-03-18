package com.github.yjgbg.vertx.boot.core

import io.vertx.core.{AbstractVerticle, DeploymentOptions, Promise, Verticle, Vertx}

case class VerticleBean(
                         deploymentOptions:DeploymentOptions = new DeploymentOptions(),
                         onStart:Promise[Unit] => Unit = it => it.complete(),
                         onStop:Promise[Unit] => Unit = it => it.complete()
                       ):
  def verticle:Verticle = new AbstractVerticle:
    override def start(startPromise: Promise[Void]): Unit = onStart(startPromise.asInstanceOf[Promise[Unit]])
    override def stop(stopPromise: Promise[Void]): Unit = onStop(stopPromise.asInstanceOf[Promise[Unit]])