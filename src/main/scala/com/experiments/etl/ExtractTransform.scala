package com.experiments.etl

trait ExtractTransform[Config, A, B] {
  def extract: Extract[Config, A]
  def transform: Transform[A, B]
}

object ExtractTransform {
  implicit class ExtractTransformOps[Config, A, B](et: ExtractTransform[Config, A, B]) {
    def ~>[C](t: Transform[B, C]): ExtractTransform[Config, A, C] =
      new ExtractTransform[Config, A, C] {
        override def extract: Extract[Config, A] = et.extract
        override def transform: Transform[A, C] = et.transform ~> t
      }

    def ~>[FinStatus](l: Load[B, FinStatus]): ETLPipeline[Config, A, B, FinStatus] =
      new ETLPipeline[Config, A, B, FinStatus] {
        override def extract: Extract[Config, A] = et.extract
        override def transform: Transform[A, B] = et.transform
        override def load: Load[B, FinStatus] = l
      }
  }
}