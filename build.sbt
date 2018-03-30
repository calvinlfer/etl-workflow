name := "etl-dsl"

version := "0.1"

scalaVersion := "2.11.12"

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.6" cross CrossVersion.binary)

libraryDependencies ++= Seq(
  "com.chuusai"   %% "shapeless" % "2.3.3",
  "org.typelevel" %% "cats-core" % "1.1.0"
)