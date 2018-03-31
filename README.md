# ETL DSL _(beta)_ #

**ETL DSL** is a simple and *opinionated* way to write type-safe Extract-Transform-Load (**ETL**) pipelines. This Domain 
Specific Language (DSL) if flexible enough to create linear pipelines which involve a single Extract source and Load 
sink all the way to stitching multiple Extract sources together and flowing the data through to multiple Load Sinks. It
is built on an immutable and functional architecture where side-effects are executed at the end-of-the-world when the 
pipeline is run. 

This is intended to be used in conjunction with Spark (especially for doing ETL) in order to minimize boilerplate and 
have the ability to see an almost whiteboard-like representation of your pipeline.

## Building Blocks ##

TODO

## Examples ##
See [here](src/main/tut/Examples.md) for examples on how to get started