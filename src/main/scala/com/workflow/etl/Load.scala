package com.workflow.etl

import cats.Functor

trait Load[-B, +Status] {
  def load(b: B): Status
}

object Load {
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

  implicit def functorForLoad[FixedInput]: Functor[Load[FixedInput, ?]] =
    new Functor[Load[FixedInput, ?]] {
      override def map[A, B](fa: Load[FixedInput, A])(f: A => B): Load[FixedInput, B] =
        new Load[FixedInput, B] {
          override def load(in: FixedInput): B = {
            val a = fa.load(in)
            val b = f(a)
            b
          }
        }
    }
}
