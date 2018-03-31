package com.experiments.etl

trait ExtractTransform[A, B] {
  def extract: Extract[A]
  def transform: Transform[A, B]
}

object ExtractTransform {
  implicit class ExtractTransformOps[A, B](et: ExtractTransform[A, B]) {
    def ~>[C](t: Transform[B, C]): ExtractTransform[A, C] =
      new ExtractTransform[A, C] {
        override def extract: Extract[A] = et.extract
        override def transform: Transform[A, C] = et.transform ~> t
      }

    def ~>[FinStatus](l: Load[B, FinStatus]): ETLPipeline[A, B, FinStatus] = new ETLPipeline[A, B, FinStatus] {
      override def extract: Extract[A] = et.extract
      override def transform: Transform[A, B] = et.transform
      override def load: Load[B, FinStatus] = l
    }
  }
}