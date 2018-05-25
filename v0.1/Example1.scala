import simplecanvas._

object Example1 {

  var lastPos = (0.0, 0.0)
  var isClosed = false
  val timeout = 20

  def loopUntilClosed(): Unit = {
    import Canvas._
    while (!isClosed) {
      awaitEvent(timeout)
      if (lastEventType == Event.MousePressed) lastPos = lastMousePos
      else if (lastEventType == Event.MouseReleased)
        line(lastPos._1, lastPos._2, lastMousePos._1, lastMousePos._2)
      else if (lastEventType == Event.WindowClosed) isClosed = true
    }
  }

  def main(args: Array[String]): Unit = {
    println("Press and release mouse to draw.")
    Canvas.setLineWidth(10)
    Canvas.setLineColor(Color.Green)
    loopUntilClosed()
    Canvas.systemExit()
  }
}
