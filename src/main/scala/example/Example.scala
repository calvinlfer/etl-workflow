package example

import com.experiments.etl.Transform._
import com.experiments.etl._

object Example extends App {
  def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
  }

  def consoleLoad[A]: Load[A, Unit] = new Load[A, Unit] {
    override def load(a: A): Unit = println(a)
  }
  // simple pipeline
  val etlPipeline: ETLPipeline[Int, String, Unit] =
    pureExtract(10) ~> lift { x: Int => x + 10 } ~> lift { x: Int => x.toString + "!" } ~> consoleLoad[String]

  etlPipeline.unsafeRunSync()

  // pipeline with multiple sources
  val intExtract: Extract[Int] = pureExtract(10)
  val strExtract: Extract[String] = pureExtract("hello")

  val multiExtract: Extract[(Int, String)] = intExtract zip strExtract

  val fancyETLPipeline: ETLPipeline[(Int, String), String, Unit] =
    multiExtract ~> lift { case (int, str) => str } ~> consoleLoad[String]

  fancyETLPipeline.unsafeRunSync()

  // pipeline with multiple sources (Extracts) and sinks (Loads)
  import cats.syntax.functor._
  val multiLoad: Load[String, Unit] = (consoleLoad[String] zip consoleLoad[String]).map { case (_, _) => () }

  val evenFancierPipeline = multiExtract ~> lift { case (int, str) => str + "!!!!" } ~> multiLoad

  evenFancierPipeline.unsafeRunSync()
}
