package com.experiments.etl

trait Transform[-A, +B] {
  def transform(a: A): B
}

object Transform {
  implicit class TransformOps[A, B](t: Transform[A, B]) {
    def ~>[C](t1: Transform[B, C]): Transform[A, C] = new Transform[A, C] {
      override def transform(a: A): C = t1.transform(t.transform(a))
    }
  }

  // smart constructor
  def lift[A, B](fn: A => B): Transform[A, B] = new Transform[A, B] {
    override def transform(a: A): B = fn(a)
  }
}