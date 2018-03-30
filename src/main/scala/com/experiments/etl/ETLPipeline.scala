package com.experiments.etl

trait ETLPipeline[Config, A, B, FinStatus] {
  def extract(c: Config): A
  def transform(a: A): B
  def load(b: B): FinStatus
}

object ETLPipeline {
  implicit class ETLPipelineOps[Config, A, B, FinStatus](p: ETLPipeline[Config, A, B, FinStatus]) {
    def unsafeRunSync(input: Config): FinStatus = {
      val a = p.extract(input)
      val b = p.transform(a)
      val fin = p.load(b)
      fin
    }
  }
}