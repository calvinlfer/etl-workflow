package com.experiments.etl
trait Load[-B, +Status] {
  def load(b: B): Status
}

object Load {
  def syncLoadMergeTuple[A, AStatus, B, BStatus](la: Load[A, AStatus], lb: Load[B, BStatus]): Load[(A, B), (AStatus, BStatus)] =
    new Load[(A, B), (AStatus, BStatus)] {
      override def load(ab: (A, B)): (AStatus, BStatus) = {
        val (a: A, b: B) = ab
        val aStatus = la.load(a)
        val bStatus = lb.load(b)
        (aStatus, bStatus)
      }
    }
}
