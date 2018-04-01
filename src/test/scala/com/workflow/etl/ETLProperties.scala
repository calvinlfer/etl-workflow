package com.workflow.etl

import org.scalacheck._
import Prop._

class ETLProperties extends Properties("ETL Pipeline Properties") {
  property("An identity pipeline passes data through untouched") = forAll { int: Int =>
    val pipeline = Extract(int) to Load(identity)
    pipeline.unsafeRunSync() == identity(int)
  }

  property("Transformers are pure functions") = forAll { int: Int =>
    val exclaim: String => String = _ + "!"
    val intToStr: Int => String = _.toString
    val composedFn: Int => String = exclaim compose intToStr
    val composedTransform = Transform(intToStr) via Transform(exclaim)
    val pipeline = Extract(int) via composedTransform to Load(identity)
    pipeline.unsafeRunSync() == composedFn(int)
  }

  property("Dependent pipelines can be sequenced") = forAll { int: Int =>
    import cats.syntax.flatMap._
    val pipelineA = Extract(int) to Load(identity)
    val pipelineB = pipelineA.flatMap(int => Extract(int) to Load(identity))
    pipelineB.unsafeRunSync() == identity(int)
  }
}
