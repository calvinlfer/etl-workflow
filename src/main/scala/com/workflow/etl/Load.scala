package com.workflow.etl

import cats.arrow.Profunctor

trait Load[-B, +Status] {
  def load(b: B): Status
}

object Load {
  def apply[A, B](fn: A => B): Load[A, B] = new Load[A, B] {
    override def load(a: A): B = fn(a)
  }

  implicit class LoadOps[A, AStatus](l: Load[A, AStatus]) {
    def zip[OtherStatus](l1: Load[A, OtherStatus]): Load[A, (AStatus, OtherStatus)] =
      new Load[A, (AStatus, OtherStatus)] {
        override def load(a: A): (AStatus, OtherStatus) = {
          val aStatus = l.load(a)
          val bStatus = l1.load(a)
          (aStatus, bStatus)
        }
      }
  }

  implicit def profunctorForLoad: Profunctor[Load] = new Profunctor[Load] {
    override def dimap[A, B, C, D](fab: Load[A, B])(f: C => A)(g: B => D): Load[C, D] = new Load[C, D] {
      override def load(c: C): D = {
        val `a -> b`: A => B = fab.load
        val a = f(c)
        val b = `a -> b`(a)
        val d = g(b)
        d
      }
    }
  }
}
