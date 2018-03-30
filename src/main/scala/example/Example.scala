package example

import com.experiments.etl.Extract._
import com.experiments.etl.Load._
import com.experiments.etl.Transform._
import com.experiments.etl._

object Example extends App {
//  // I wish it was Extract[Nothing, A] but invariance :-(
//  type PureExtract[A] = Extract[A, A]
//
//  def pureExtract[A](a: => A): PureExtract[A] = new Extract[A, A] {
//    override def extract(config: A): A = a
//  }
//
//  def consoleLoad[A]: Load[A, Unit] = new Load[A, Unit] {
//    override def load(a: A): Unit = println(a)
//  }
//
//  // simple pipeline
//  val etlPipeline: ETLPipeline[_, Int, String, Unit] =
//    pureExtract(10) ~> transform { x: Int => x + 10 } ~> transform { x: Int => x.toString } ~> consoleLoad[String]
//
//  // pipeline with multiple sources
//  val intExtract: PureExtract[Int] = pureExtract(10)
//  val strExtract: PureExtract[String] = pureExtract("hello")
//
//  val multiExtract: PureExtract[(Int, String)] = intExtract zip strExtract
//
//  val fancyETLPipeline: ETLPipeline[(Int, String), (Int, String), String, Unit] =
//    multiExtract ~> transform { case (int, str) => str } ~> consoleLoad[String]
//
//  // fancier pipeline with multiple sources and sinks
//  val multiLoad: Load[(Int, String), (Unit, Unit)] = consoleLoad[Int] zip consoleLoad[String]
//  multiExtract ~> transform(identity) ~> multiLoad
//
//  // it becomes messy without HLists :-(
//  val doubleExtract: PureExtract[Double] = pureExtract(1.0)
//  val moreMultiExtract: Extract[((Int, String), Double), ((Int, String), Double)] = intExtract zip strExtract zip doubleExtract
//
//  val moreMultiLoad: Load[((Int, String), Boolean), ((Unit, Unit), Unit)] =
//    consoleLoad[Int] zip consoleLoad[String] zip consoleLoad[Boolean]
//
//  val messyPipeline = {
//    def fn1(t: (Int, String, Double)): (Int, String, Boolean) = {
//      val (int, str, double) = t
//      (int + 1, str + "!", double == 1)
//    }
//
//    def fn2(t: (Int, String, Boolean)): ((Int, String), Boolean) = {
//      val (int, str, bool) = t
//      ((int + 1, str + "!"), !bool)
//    }
//
//    import cats.syntax.functor._
//    moreMultiExtract.map { case ((i, s), d) => (i, s, d) } ~> transform(fn1) ~> transform(fn2) ~> moreMultiLoad
//  }
//
//  messyPipeline.unsafeRunSync(input = ((0, "doesn't matter"), 1.0))
}
