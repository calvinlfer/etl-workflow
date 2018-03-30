package com.experiments.etl

trait ETLPipeline[Config, A, B, FinStatus] {
  def extract: Extract[Config, A]
  def transform: Transform[A, B]
  def load: Load[B, FinStatus]
}

object ETLPipeline {
  implicit class ETLPipelineOps[Config, A, B, FinStatus](p: ETLPipeline[Config, A, B, FinStatus]) {
    def unsafeRunSync(): FinStatus = {
      val a = p.extract.out
      val b = p.transform.transform(a)
      val st = p.load.load(b)
      st
    }
  }
}