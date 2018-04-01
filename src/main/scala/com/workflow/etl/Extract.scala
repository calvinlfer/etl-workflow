package com.workflow.etl

import cats.Functor

trait Extract[+A] {
  def produce: A
}

object Extract {
  implicit class ExtractOps[A](e: Extract[A]) {
    def via[B](t: Transform[A, B]): Extract[B] = new Extract[B] {
      override def produce: B = {
        val a = e.produce
        val b = t.transform(a)
        b
      }
    }

    def to[AStatus](l: Load[A, AStatus]): ETLPipeline[AStatus] = new ETLPipeline[AStatus] {
      override def execute(): AStatus = {
        val a = e.produce
        val aStatus = l.load(a)
        aStatus
      }
    }

    def zip[BConfig, B, CConfig, C](e1: Extract[B]): Extract[(A, B)] =
      new Extract[(A, B)] {
        override def produce: (A, B) = (e.produce, e1.produce)
      }
  }

  implicit val functorForExtract: Functor[Extract] = new Functor[Extract] {
    override def map[A, B](fa: Extract[A])(f: A => B): Extract[B] =
      new Extract[B] {
        override def produce: B = f(fa.produce)
      }
  }
}