package example

import cats.syntax.profunctor._
import com.workflow.etl._

object Example extends App {
  def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
  }

  def consoleLoad[A]: Load[A, Unit] = new Load[A, Unit] {
    override def load(a: A): Unit = println(a)
  }
  // simple pipeline
  val etlPipeline: ETLPipeline[Unit] =
    pureExtract(10) via Transform[Int, Int] { x: Int =>
      x + 10
    } via Transform[Int, String] { x: Int =>
      x.toString + "!"
    } to consoleLoad[String]

  etlPipeline.unsafeRunSync()

  // pipeline with multiple sources
  val intExtract: Extract[Int] = pureExtract(10)
  val strExtract: Extract[String] = pureExtract("hello")

  val multiExtract: Extract[(Int, String)] = intExtract zip strExtract

  def transformFn: Transform[(Int, String), String] = Transform { case (int, str) => str }
  val fancyETLPipeline: ETLPipeline[Unit] =
    multiExtract via transformFn to consoleLoad[String]

  fancyETLPipeline.unsafeRunSync()

  // pipeline with multiple sources (Extracts) and sinks (Loads)
  val multiLoad: Load[String, Unit] = (consoleLoad[String] zip consoleLoad[String]).rmap { case (_, _) => () }
  val anotherTransformFn: Transform[(Int, String), String] = Transform { case (int, str) => str + "!!!!" }
  val evenFancierPipeline = multiExtract via anotherTransformFn to multiLoad

  evenFancierPipeline.unsafeRunSync()

  // super simple pipeline (Extract Load)
  strExtract to consoleLoad[String] unsafeRunSync ()
}
