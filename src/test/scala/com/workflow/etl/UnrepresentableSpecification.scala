package com.workflow.etl

import org.scalatest.{ FunSpec, MustMatchers }

class UnrepresentableSpecification extends FunSpec with MustMatchers {
  describe("Illegal states are unrepresentable") {
    it("is impossible to connect an Int Extract to a String Load") {
      val extractInt: Extract[Int] = Extract(10)
      val loadString: Load[String, Unit] = Load(println)
      "extractInt to loadString" mustNot typeCheck
    }

    it("is impossible to connect a transformed Int Extract to to a String Load") {
      val ten = "10"
      val extractString: Extract[String] = Extract(ten)
      val transform: Transform[String, Int] = Transform(s => s.toInt)
      val loadString: Load[String, String] = Load(identity)
      (extractString to loadString unsafeRunSync ()) mustBe ten
      "extractString via transform to loadString" mustNot typeCheck
    }

    it("is impossible to connect a Transform to a Load when the output of Transform does not match the input of Load") {
      val transform: Transform[Int, String] = Transform(i => i.toString)
      val loadString: Load[Int, String] = Load(_.toString)
      "transform to loadString" mustNot typeCheck
    }

    it(
      "is impossible to connect a Extract to a Transform when the input of Transform does not match the output of Extract"
    ) {
      val extract: Extract[String] = Extract("hello")
      val transform: Transform[Int, String] = Transform(_.toString)
      "extract via transform" mustNot typeCheck
    }
  }
}
