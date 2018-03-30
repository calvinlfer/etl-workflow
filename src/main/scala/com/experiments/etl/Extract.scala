package com.experiments.etl

trait Extract[Config, A] {
  def in: Config
  def out: A
}

object Extract {
  implicit class ExtractOps[Config, A](e: Extract[Config, A]) {
    def ~>[B](t: Transform[A, B]): ExtractTransform[Config, A, B] =
      new ExtractTransform[Config, A, B] {
        override def extract: Extract[Config, A] = e
        override def transform: Transform[A, B] = t
      }

    def zip[BConfig, B, CConfig, C](e1: Extract[BConfig, B]): Extract[(Config, BConfig), (A, B)] =
      syncExtractMergeTuple(e, e1)
  }

  private def syncExtractMergeTuple[AConfig, A, BConfig, B](e1: Extract[AConfig, A], e2: Extract[BConfig, B]): Extract[(AConfig, BConfig), (A, B)] =
    new Extract[(AConfig, BConfig), (A, B)] {
      override def in: (AConfig, BConfig) = (e1.in, e2.in)
      override def out: (A, B) = (e1.out, e2.out)
    }
}