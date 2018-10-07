package com.workflow.etl

import cats.Applicative

trait Extract[+A] {
  def produce: A
}

object Extract {
  implicit val applicativeForExtract: Applicative[Extract] = new Applicative[Extract] {
    override def pure[A](a: A): Extract[A] = new Extract[A] {
      override def produce: A = a
    }
    override def ap[A, B](fab: Extract[A => B])(fa: Extract[A]): Extract[B] = new Extract[B] {
      override def produce: B = {
        val a = fa.produce
        val `a->b` = fab.produce
        `a->b`(a)
      }
    }
  }

  def apply[A](a: A): Extract[A] = Applicative[Extract].pure[A](a)

  implicit class ExtractOps[A](e: Extract[A]) {
    def via[B](t: Transform[A, B]): Extract[B] = new Extract[B] {
      override def produce: B = {
        val a = e.produce
        val b = t.transform(a)
        b
      }
    }

    def >>[B](t: Transform[A, B]): Extract[B] = via(t)

    def to[AStatus](l: Load[A, AStatus]): ETLPipeline[AStatus] = new ETLPipeline[AStatus] {
      override def execute(): AStatus = {
        val a = e.produce
        val aStatus = l.load(a)
        aStatus
      }
    }

    def >>>[AStatus](l: Load[A, AStatus]): ETLPipeline[AStatus] = to(l)

    def zip[BConfig, B, CConfig, C](e1: Extract[B]): Extract[(A, B)] = Applicative[Extract].map2(e, e1)(Tuple2(_, _))

    def <*>[BConfig, B, CConfig, C](e1: Extract[B]): Extract[(A, B)] = zip(e1)
  }
}
