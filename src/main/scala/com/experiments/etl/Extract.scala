package com.experiments.etl

import cats.Functor

trait Extract[-Config, +A] {
  def extract(c: Config): A
}

object Extract {
  implicit class ExtractOps[Config, A](e: Extract[Config, A]) {
    def ~>[B](t: Transform[A, B]): ExtractTransform[Config, A, B] =
      new ExtractTransform[Config, A, B] {
        override def extract(c: Config): A = e.extract(c)
        override def transform(a: A): B = t.transform(a)
      }

    def zip[BConfig, B, CConfig, C](e1: Extract[BConfig, B]): Extract[(Config, BConfig), (A, B)] =
      syncExtractMergeTuple(e, e1)
  }

  implicit def extractOutputFunctor[Config]: Functor[Extract[Config, ?]] = new Functor[Extract[Config, ?]] {
    override def map[A, B](fa: Extract[Config, A])(f: A => B): Extract[Config, B] = new Extract[Config, B] {
      override def extract(c: Config): B = {
        val a = fa.extract(c)
        val b = f(a)
        b
      }
    }
  }

  private def syncExtractMergeTuple[AConfig, A, BConfig, B](e1: Extract[AConfig, A], e2: Extract[BConfig, B]): Extract[(AConfig, BConfig), (A, B)] =
    new Extract[(AConfig, BConfig), (A, B)] {
      override def extract(abConfig: (AConfig, BConfig)): (A, B) = {
        val (aConfig, bConfig) = abConfig
        val a = e1.extract(aConfig)
        val b = e2.extract(bConfig)
        (a, b)
      }
    }
}