lazy val Version = "0.1"
lazy val Name    = "simplecanvas"

name := Name
organization := "se.bjornregnell"
version := Version
scalaVersion := "2.12.6"
fork := true

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
//  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
//  "-Ywarn-value-discard",
//  "-Ywarn-unused"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-implicits",
  "-groups",
  "-doc-title", Name,
  "-doc-footer", "BSD 2-clause. (c) Bjorn Regnell",
  "-sourcepath", (baseDirectory in ThisBuild).value.toString,
  "-doc-version", Version,
  "-doc-root-content", (baseDirectory in ThisBuild).value.toString + "/src/rootdoc.txt",
  "-doc-source-url", s"https://github.com/bjornregnell/simplecanvas/tree/master€{FILE_PATH}.scala"
)
