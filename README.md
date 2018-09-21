# simplecanvas

This repo is a playground that was created to experiment with beginner friendly
game programming in Scala. The released results of these experiments are available
here: https://github.com/lunduniversity/introprog-scalalib

This repo includes also an JavaFX version of PixelWindow that is **not** included in
later version of https://github.com/lunduniversity/introprog-scalalib, as it turns out that JavaFX will not be
included out-of-the-box on the JVM platform from JDK11:
 * https://blogs.oracle.com/java-platform-group/the-future-of-javafx-and-other-java-client-roadmap-updates

 * https://gluonhq.com/gluon-and-javafx/

## How to play with the code in this repo?

* Download the latest jar from the release page:

* Put the jar on your classpath manually:
```
> scalac -cp jarfile.jar yourcode.scala
> scala -cp "jarfile.jar:." yourpackage.YourMain
```

* Or use `sbt console` after placing the jar in a `lib` sub-folder you can e.g.:
```
scala> val w = new introprog.PixelWindow()
w: introprog.PixelWindow = introprog.PixelWindow@7cf7ecf0

scala> w.drawText("HELLO WORLD!", 100, 100)

scala> w.fill(10,10,20,30)

```


## How to build a jar yourself?

Clone this repo and then enter `sbt package` in terminal. You need the Scala Build Tool on your path: https://www.scala-sbt.org/


## How to contribute?

* Contributions are welcome after discussions in a new issue thread.

* Preferably contribution experiments that have shown to be feasible, should also be incorporated in  https://github.com/lunduniversity/introprog-scalalib  

* Contact bjorn.regnell@cs.lth.se for further information on how to contribute.
