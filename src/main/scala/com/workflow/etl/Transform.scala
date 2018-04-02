package com.workflow.etl

import cats.arrow.Profunctor

trait Transform[-A, +B] {
  def transform(a: A): B
}

object Transform {
  implicit class TransformOps[A, B](t: Transform[A, B]) {
    def via[C](t1: Transform[B, C]): Transform[A, C] = new Transform[A, C] {
      override def transform(a: A): C = t1.transform(t.transform(a))
    }

    def to[FinStatus](loadB: Load[B, FinStatus]): Load[A, FinStatus] = new Load[A, FinStatus] {
      override def load(a: A): FinStatus = {
        val b = t.transform(a)
        loadB.load(b)
      }
    }
  }

  // smart constructor
  def apply[A, B](fn: A => B): Transform[A, B] = new Transform[A, B] {
    override def transform(a: A): B = fn(a)
  }

  implicit val profunctorForTransform: Profunctor[Transform] = new Profunctor[Transform] {
    override def dimap[A, B, C, D](fab: Transform[A, B])(f: C => A)(g: B => D): Transform[C, D] = new Transform[C, D] {
      override def transform(c: C): D = {
        val `a -> b`: A => B = fab.transform
        val a = f(c)
        val b = `a -> b`(a)
        val d = g(b)
        d
      }
    }
  }
}
