## Examples ##

Here are some examples that will help you to get started

### Linear Pipelines ###

Here is an example of how to create a simple Extract-Transform-Load pipeline where data flows from an `Extract` source 
through to a set of `Transformer`s that operate on the data and then to a `Load` sink thereby creating an `ETLPipeline`
which can be run to begin the workflow.

```tut
import com.workflow.etl._

// An easy way to build an Extract[A] which takes a value you provide and pushes it down the pipeline
def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
}

val tenExtract: Extract[Int] = pureExtract(10)

// convert an int to a double and then a string and then add an exclamation mark
val toDbl: Transform[Int, Double] = Transform(i => i.toDouble)
val toStr: Transform[Double, String] = Transform(d => d.toString)
val exclaim: Transform[String, String] = Transform(s => s + "!")

// compose transforms together
val transform: Transform[Int, String] = toDbl via toStr via exclaim

// load data to the screen
val consoleLoad: Load[String, Unit] = new Load[String, Unit] {
    def load(in: String): Unit = println(in)
}

// Create ETL pipeline
val etlPipeline = tenExtract via transform to consoleLoad

// run ETL pipeline
etlPipeline.unsafeRunSync()
```

#### Aside ####
**ETL Workflow** places a heavy emphasis on type-safety so you cannot hook up a `Extract` which produces a `String` to a `Load` 
which expects an `Int`.

```tut
val stringExtract = new Extract[String] {
    override def produce: String = "type-safety is one of the primary concerns"
}

val intLoad = new Load[Int, Unit] {
    def load(in: Int): Unit = println(in)
}
```

The following will cause a *compile-time error* because you cannot connect an `Extract[String]` to a `Load[Int]`:

```tut:fail
stringExtract to intLoad
```

Only an `Extract[String]` can be connected to a `Load[String]`:

```tut
val stringLoad = new Load[String, Unit] {
    def load(in: String): Unit = println(in)
}

val etlPipeline = stringExtract to stringLoad
etlPipeline.unsafeRunSync()
```

### Complex Pipelines ###

Here is an example of how to create a pipeline that stitches together multiple `Extract` data sources and flows data to
through a single `Load` sink.

```tut
import com.workflow.etl._
import cats.syntax.functor._

// An easy way to build an Extract[A] which takes a value you provide and pushes it down the pipeline
def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
}

val intExtract: Extract[Int] = pureExtract(10)
val strExtract: Extract[String] = pureExtract("hello")
val dblExtract: Extract[Double] = pureExtract(20.0)

// stitch and remove extra tuple nesting
val stitchedExtract: Extract[(Int, String, Double)] = 
    (intExtract zip strExtract zip dblExtract).map { case ((int, str), dbl) => (int, str, dbl) } 
    
// Load sink which consumes strings
val consoleLoad: Load[String, Unit] = new Load[String, Unit] {
    def load(in: String): Unit = println(in)
}

val transform: Transform[(Int, String, Double), String] = Transform { case (int, str, dbl) => s"$int $str! $dbl" }

val etlPipeline = stitchedExtract via transform to consoleLoad
etlPipeline.unsafeRunSync()
```

You can feed the data to multiple `Load` sinks as well:

```tut
val exclaimLoad: Load[String, String] = new Load[String, String] {
    def load(in: String): String = s"$in!"
}

val combinedLoad: Load[String, (Unit, String)] = consoleLoad zip exclaimLoad
val etlPipeline = stitchedExtract via transform to combinedLoad
etlPipeline.unsafeRunSync()
```

### Sequencing pipelines ###

You can compose ETL pipelines such that ETL pipeline A runs and feeds it result to ETL pipeline B:

```tut
import cats.syntax.flatMap._

// An easy way to build an Extract[A] which takes a value you provide and pushes it down the pipeline
def pureExtract[A](a: => A): Extract[A] = new Extract[A] {
    override def produce: A = a
}

val etlPipelineA = stitchedExtract via transform to combinedLoad

val etlPipelineB = etlPipelineA.flatMap { pipelineAResult: (Unit, String) =>
    val strInput = pipelineAResult._2
    pureExtract(strInput) to consoleLoad
}

etlPipelineB.unsafeRunSync()
```