## Examples ##

Here are some examples that will help you to get started

## Linear Pipelines ##

Here is an example of how to create a simple Extract-Transform-Load pipeline where data flows from an `Extract` source 
through to a set of `Transforme`rs that operate on the data and then to a `Load` sink thereby creating an `ETLPipeline`
which can be run to begin the workflow.

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
etlPipeline.unsafeRunSync()
```

#### Aside ####
**ETL DSL** places a heavy emphasis on type-safety so you cannot hook up a `Extract` which produces a `String` to a `Load` 
which expects an `Int`.

```tut
import com.experiments.etl._
val stringExtract = new Extract[String] {
    override def produce: String = "type-safety is one of the primary concerns"
}

val intLoad = new Load[Int, Unit] {
    def load(in: Int): Unit = println(in)
}
```

The following will cause a *compile-time error* because you cannot connect an `Extract[String]` to a `Load[Int]`

```tut:fail

stringExtract ~> intLoad
```

Only an `Extract[String]` can be connected to a `Load[String]`

```tut
val stringLoad = new Load[String, Unit] {
    def load(in: String): Unit = println(in)
}

stringExtract ~> stringLoad
```