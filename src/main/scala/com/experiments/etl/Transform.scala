package com.experiments.etl

import cats.Functor

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

  implicit def functorForTransform[G]: Functor[Transform[G, ?]] = new Functor[Transform[G, ?]] {
    override def map[A, B](fa: Transform[G, A])(f: A => B): Transform[G, B] =
      new Transform[G, B] {
        override def transform(g: G): B = {
          val a = fa.transform(g)
          val b = f(a)
          b
        }
      }
  }
}