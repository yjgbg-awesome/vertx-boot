package com.github.yjgbg.vertx.boot.vertxCps

trait CpsSyntax:
  given VertxCpsMonad = VertxCpsMonad
object CpsSyntax extends CpsSyntax
  
