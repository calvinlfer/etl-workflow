package com.experiments.etl

trait Load[B, Status] {
  def load(b: B): Status
}
