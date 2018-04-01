package com.workflow.etl

import cats.{Functor, Monad}

import scala.annotation.tailrec

trait ETLPipeline[FinStatus] {
  def execute(): FinStatus
}

object ETLPipeline {
  implicit class ETLPipelineOps[FinStatus](p: ETLPipeline[FinStatus]) {
    def unsafeRunSync(): FinStatus = p.execute()
  }

  implicit val functorForETLPipeline: Functor[ETLPipeline] = new Functor[ETLPipeline] {
    override def map[A, B](fa: ETLPipeline[A])(f: A => B): ETLPipeline[B] =
      new ETLPipeline[B] {
        override def execute(): B = {
          val a = fa.execute()
          val b = f(a)
          b
        }
      }
  }

  // Allow sequencing of dependent pipelines
  implicit val monadForETLPipeline: Monad[ETLPipeline] = new Monad[ETLPipeline] {
    override def pure[A](x: A): ETLPipeline[A] = new ETLPipeline[A] {
      override def execute(): A = x
    }

    override def flatMap[A, B](fa: ETLPipeline[A])(f: A => ETLPipeline[B]): ETLPipeline[B] =
      f(fa.execute())

    @tailrec
    override def tailRecM[A, B](a: A)(f: A => ETLPipeline[Either[A, B]]): ETLPipeline[B] = {
      f(a).execute() match {
        case Left(resA) => tailRecM(resA)(f)
        case Right(resB) => pure(resB)
      }
    }
  }
}
