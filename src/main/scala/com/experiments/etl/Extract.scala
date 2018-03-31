package com.experiments.etl

import cats.Functor

trait Extract[+A] {
  def produce: A
}

object Extract {
  implicit class ExtractOps[A](e: Extract[A]) {
    def ~>[B](t: Transform[A, B]): ExtractTransform[A, B] =
      new ExtractTransform[A, B] {
        override def extract: Extract[A] = e
        override def transform: Transform[A, B] = t
      }

    def ~>[AStatus](l: Load[A, AStatus]): ETLPipeline[A, A, AStatus] = new ETLPipeline[A, A, AStatus] {
      override def extract: Extract[A] = e
      override def transform: Transform[A, A] = Transform.lift(identity)
      override def load: Load[A, AStatus] = l
    }

    def zip[BConfig, B, CConfig, C](e1: Extract[B]): Extract[(A, B)] =
      syncExtractMergeTuple(e, e1)
  }

  private def syncExtractMergeTuple[A, B](e1: Extract[A], e2: Extract[B]): Extract[(A, B)] =
    new Extract[(A, B)] {
      override def produce: (A, B) = (e1.produce, e2.produce)
    }

  implicit val functorForExtract: Functor[Extract] = new Functor[Extract] {
    override def map[A, B](fa: Extract[A])(f: A => B): Extract[B] =
      new Extract[B] {
        override def produce: B = f(fa.produce)
      }
  }
}