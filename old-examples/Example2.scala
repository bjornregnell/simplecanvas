import simplecanvas._

object Example2 {

  object State {
    val xMax = 1100.0
    val yMax = 400.0
    val xMin = 100.0
    val yMin = 100.0

    val millisPerFrame        = 10
    val framesPerSecond       = (1000.0 / millisPerFrame).round
    val maxWaitForEventMillis = 1

    var x    = 100.0
    var y    = 100.0
    var step = 25.0
    var dy   = 0.0
    var dx   = 0.0

    val colvec = Vector(Color.Red, Color.Green, Color.Blue)
    var colorIndex = 0

    var isClosed = false
  }

  import State._

  def toggleColor(): Unit = {
    colorIndex = (colorIndex + 1) % colvec.size
    Canvas.setLineColor(colvec(colorIndex))
  }

  def now: Long = System.nanoTime
  def elapsedMillis(since: Long): Double = (now - since) / 1e6

  def init(): Unit = {
    Canvas.show // show window and request focus
    Canvas.setLineWidth(20)
    toggleColor()
    println(s"Looping at $framesPerSecond frames/s, $millisPerFrame ms/frame")
  }

  def showLastEvent: String =
    s"""
     |  lastEventType: ${Canvas.lastEventType}
     |  lastKeyCode:   ${Canvas.lastKeyCode}
     |  lastKeyText:   ${Canvas.lastKeyText}
     |  lastMousePos:  ${Canvas.lastMousePos}
     """.stripMargin


  def handleLastEvent(): Unit = {
    if (Canvas.lastEventType == Event.KeyPressed) {
      if (Canvas.lastKeyCode == "DOWN")  { dx = 0;     dy = step  }
      if (Canvas.lastKeyCode == "UP")    { dx = 0;     dy = -step }
      if (Canvas.lastKeyCode == "RIGHT") { dx = step;  dy = 0     }
      if (Canvas.lastKeyCode == "LEFT")  { dx = -step; dy = 0     }
      toggleColor()
    } else if (Canvas.lastEventType == Event.WindowClosed) {
      println("\n  Window Closed!")
      isClosed = true
    }
  }

  def drawNextFrame(): Unit = {
    if (x > xMax)      { x = xMin; toggleColor() }
    else if (x < xMin) { x = xMax; toggleColor() }

    if (y > yMax)      { y = yMin; toggleColor() }
    else if (y < yMin) { y = yMax; toggleColor() }

    Canvas.line(x, y, x + dx, y + dy)

    x += dx
    y += dy
  }

  def loopUntilClosed(): Unit = {
    while (!isClosed) {
      val t0 = now

      Canvas.awaitEvent(maxWaitForEventMillis)

      if (Canvas.lastEventType != Event.Undefined) {
        println(showLastEvent)
        handleLastEvent()
      }

      drawNextFrame()

      val elapsed = elapsedMillis(since = t0)
      val delayMillis = millisPerFrame - elapsed

      if (elapsed > millisPerFrame)
        print(s" lagging ${((elapsed - millisPerFrame)*100).round/100.0} ms ")
      if (delayMillis > 0.0) Canvas.delay(delayMillis.round.toInt)
    }
  }

  def main(args: Array[String]): Unit = {
    init()
    loopUntilClosed()

    /* Open another canvas window: */
    val w = new CanvasWindow("Closing Alert",(640,400),Color.Red,false)
    w.writeText("Waiting for this window to close...",10,200)
    do w.awaitEvent(1000) while (w.lastEventType != Event.WindowClosed)

    Canvas.stopAllWindows
  }
}
