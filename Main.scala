object Main {

  def main(args: Array[String]): Unit = {
    println(s"main started with args=$args")

    Canvas.line(10,10)(100,100)

    import Canvas._

    line(100, 100, 200, 100)
    rect(200, 200)(30, 70)

    val cw = new CanvasWindow("ännu ett fönster")
    val (p, dxy) = ((200,200), (30,70))
    cw.rect(p)(dxy)
    val ws = (1 to 3).map{ i => new CanvasWindow(s"fönster $i")}
  }
}
