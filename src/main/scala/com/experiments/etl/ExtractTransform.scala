package com.experiments.etl

trait ExtractTransform[Config, A, B] {
  def extract(c: Config): A
  def transform(a: A): B
}

object ExtractTransform {
  implicit class ExtractTransformOps[Config, A, B](et: ExtractTransform[Config, A, B]) {
    def ~>[C](t: Transform[B, C]): ExtractTransform[Config, A, C] =
      new ExtractTransform[Config, A, C] {
        override def extract(c: Config): A = et.extract(c)
        override def transform(a: A): C = t.transform(et.transform(a))
      }

    def ~>[FinStatus](l: Load[B, FinStatus]): ETLPipeline[Config, A, B, FinStatus] =
      new ETLPipeline[Config, A, B, FinStatus] {
        override def extract(c: Config): A = et.extract(c)
        override def transform(a: A): B = et.transform(a)
        override def load(b: B): FinStatus = l.load(b)
      }
  }
}