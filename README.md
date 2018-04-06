# simplecanvas

```
$ scala -cp simplecanvas.jar

Welcome to Scala 2.12.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_161).
Type in expressions for evaluation. Or try :help.

scala> import simplecanvas._
import simplecanvas._

scala> Canvas.line(10,20,100,200)

```

## What is it?
*  `simplecanvas` is a minimal [Scala](http://www.scala-lang.org/) library for beginner-friendly 2D game programming used in undergraduate teaching at [Lund University](http:cs.lth.se/pgk).

* With `simplecanvas` You can make games even if you only know some very basic programming concepts like variables, if-statements and while-loops. You don't need to know e.g. how to make your own classes or how to use a GUI framework. Just do `Canvas.line(0,0,100,100)` and all the GUI stuff is seamlessly taken care of by this library.

* `simplecanvas` has no extra dependencies except Scala on the  [JVM](http://www.oracle.com/technetwork/java/javase/downloads/index.html) with [JavaFX](https://en.wikipedia.org/wiki/JavaFX).

* `simplecanvas` runs in the Scala REPL or a normal Scala main app, just download the jar and `import simplecanvas._` and start hacking.

* Contributions are welcome! Simple build and package is enabled by [sbt](https://www.scala-sbt.org/) (see further instructions below).

## How to use?

TODO

## How to build?

TODO

## How to contribute?

TODO
