package com.experiments.etl
trait Load[-B, +Status] {
  def load(b: B): Status
}

object Load {
  implicit class LoadOps[A, AStatus](l: Load[A, AStatus]) {
    def zip[B, BStatus](l1: Load[B, BStatus]): Load[(A, B), (AStatus, BStatus)] = syncLoadMergeTuple(l, l1)
  }

  private def syncLoadMergeTuple[A, AStatus, B, BStatus](la: Load[A, AStatus], lb: Load[B, BStatus]): Load[(A, B), (AStatus, BStatus)] =
    new Load[(A, B), (AStatus, BStatus)] {
      override def load(ab: (A, B)): (AStatus, BStatus) = {
        val (a: A, b: B) = ab
        val aStatus = la.load(a)
        val bStatus = lb.load(b)
        (aStatus, bStatus)
      }
    }
}
