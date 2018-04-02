package simplecanvas

object Example {

  import simplecanvas._

  def main(args: Array[String]): Unit = {
    println(s"main started with args=$args")

    Canvas.line(10,10,100,100)

    import Canvas._
    line(100, 100, 200, 100)

    val cw = new CanvasWindow("A second window.")
    cw.line(100,100,200,100)

    val ws = (3 to 5).map{ i => new CanvasWindow(s"Yet another window $i")}
    for ((w,i) <- ws.zipWithIndex) w.line(0,0,(i+1)*100,(i+1)*200)
  }
}
