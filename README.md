# ETL Workflow _(beta)_ #

[![Build Status](https://travis-ci.org/calvinlfer/etl-workflow.svg?branch=master)](https://travis-ci.org/calvinlfer/etl-workflow)

**ETL Workflow** is a simple and *opinionated* way to help you structure type-safe Extract-Transform-Load (**ETL**) 
pipelines. This Domain Specific Language (DSL) is flexible enough to create linear pipelines which involve a single 
`Extract` source and `Load` sink 

```
Extract source A ~> Transform A to B ~> Load B (sink 1)
```

all the way to stitching multiple Extract sources together and flowing the data through to multiple Load sinks

```

Extract source A ~>                               ~> Load D (sink 1)
                   \                             /
Extract source B    ~> Transform (A, B, C) to D ~>   Load D (sink 2)
                   /                             \
Extract source C ~>                               ~> Load D (sink 3)

``` 

It is built on an immutable and functional architecture where side-effects are executed at the end-of-the-world when the 
pipeline is run. 

This is intended to be used in conjunction with Spark (especially for doing ETL) in order to minimize boilerplate and 
have the ability to see an almost whiteboard-like representation of your pipeline.

## Building Blocks ##

An ETL pipeline consists of the following building blocks:

#### `Extract[A]` ####
A producer of a single element of data whose type is `A`. This is the start of the ETL pipeline, you can connect this
to `Transform`ers or to a `Load[A, AStatus]` to create an `ETLPipeline[AStatus]` that can be run.

#### `Transform[A, B]` ####
A transformer of a an element `A` to `B` you can attach these after an `Extract[A]` or before a `Load[B]`

#### `Load[B, BStatus]` ####
The end of the pipeline which takes data `B` flowing through the pipeline and consumes it and produces a status 
`BStatus` which indicates whether consumption happens successfully

#### `ETLPipeline[ConsumeStatus]` ####
This represents the fully created ETL pipeline which can be executed using `unsafeRunSync()` to produce a 
`ConsumeStatus` which indicates whether the pipeline has finished successfully.

**Note:** At the end of the day, these building blocks are a reification of values and functions. You can build an 
ETL pipeline out of functions and values but it helps to have a Domain Specific Language to increase readability.

## Examples ##
See [here](src/main/tut/Examples.md) for examples on how to get started

### Inspiration ###

- [Mario](https://github.com/intentmedia/mario)
- [Akka Streams](https://doc.akka.io/docs/akka/2.5/stream/index.html)
- [Monix Observables](https://monix.io)


### Release process ###
Make sure you have the correct [Bintray credentials](http://queirozf.com/entries/publishing-an-sbt-project-onto-bintray-an-example)
before proceeding:

```bash
sbt release
```

This will automatically create a Git Tag and publish the library to Bintray for all Scala versions.