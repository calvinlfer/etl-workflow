name := "etl-dsl"

version := "0.1"

scalaVersion := "2.11.12"

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.6" cross CrossVersion.binary)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.1.0"
)

// Ignore failures when downloading sources and documentation for SBT plugins (but not the main artifact)
updateConfiguration in updateSbtClassifiers := (updateConfiguration in updateSbtClassifiers).value.withMissingOk(true)

enablePlugins(TutPlugin)

tutNameFilter := """.*\.(md)""".r