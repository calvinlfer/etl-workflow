## Examples ##

Here are some examples that will help you to get started

## Linear Pipelines ##

Here is an example of how to create a simple Extract-Load pipeline where data flows from an Extract source to a Load 
sink.

```tut
import com.experiments.etl._

// An easy way to build an Extract[A] which takes a value you provide and pushes it down the pipeline
def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
}

val tenExtract: Extract[Int] = pureExtract(10)

// provides an easy way to lift normal functions to work in pipelines
import Transform._

// convert an int to a double and then a string and then add an exclamation mark
val toDbl: Transform[Int, Double] = lift(i => i.toDouble)
val toStr: Transform[Double, String] = lift(d => d.toString)
val exclaim: Transform[String, String] = lift(s => s + "!")

// compose transforms together
val transform: Transform[Int, String] = toDbl ~> toStr ~> exclaim

// load data to the screen
val consoleLoad: Load[String, Unit] = new Load[String, Unit] {
    def load(in: String): Unit = println(in)
}

// Create ETL pipeline
val etlPipeline = tenExtract ~> transform ~> consoleLoad

// run ETL pipeline
import ETLPipeline._
etlPipeline.unsafeRunSync()
```