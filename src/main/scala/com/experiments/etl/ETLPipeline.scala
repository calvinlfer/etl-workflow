package com.experiments.etl

trait ETLPipeline[A, B, FinStatus] {
  def extract: Extract[A]
  def transform: Transform[A, B]
  def load: Load[B, FinStatus]
}

object ETLPipeline {
  implicit class ETLPipelineOps[Config, A, B, FinStatus](p: ETLPipeline[A, B, FinStatus]) {
    def unsafeRunSync(): FinStatus = {
      val a = p.extract.produce
      val b = p.transform.transform(a)
      val st = p.load.load(b)
      st
    }
  }
}