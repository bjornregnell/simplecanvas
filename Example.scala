import simplecanvas._

object Example {

  object State {
    val xMax = 1100.0
    val yMax = 400.0
    val xMin = 100.0
    val yMin = 100.0

    val millisPerFrame        = 5
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

  def now = System.nanoTime
  def elapsedMillis(since: Long): Double = (now - since) / 1e6

  def init(): Unit = {
    Canvas.show // show window and request focus
    Canvas.setLineWidth(20)
    toggleColor()
    println(s"Looping at $framesPerSecond frame/s, $millisPerFrame ms/frame")
  }

  def handleEvent(): Unit = {
    import Canvas._
    import javafx.scene.input.KeyCode

    if (lastEventType == Event.KeyPressed) {
      if (lastKeyCode == KeyCode.DOWN)  { dx = 0;     dy = step  }
      if (lastKeyCode == KeyCode.UP)    { dx = 0;     dy = -step }
      if (lastKeyCode == KeyCode.RIGHT) { dx = step;  dy = 0     }
      if (lastKeyCode == KeyCode.LEFT)  { dx = -step; dy = 0     }
      //if (lastKeyCode == KeyCode.F11) setFullScreen(!isFullScreen)
      toggleColor()
    } else if (lastEventType == Event.WindowClosed) {
      println("Window Closed!")
      isClosed = true
    } else if (lastEventType != Event.Undefined) {
     println(s"""
       lastEventType: $lastEventType
       lastKeyCode:  "$lastKeyCode"
       lastKeyText:  "$lastKeyText"
       KEY           "${if (lastKeyCode == KeyCode.UNDEFINED && lastKeyText != "") lastKeyText.toUpperCase else lastKeyCode}"
       lastMousePos $lastMousePos
       (x,y)=($x,$y) (dx,dy)=($dx,$dy) size=$size """)
    } //else print(".")
  }

  def updateState(): Unit = {
    if (x > xMax)      { x = xMin; toggleColor() }
    else if (x < xMin) { x = xMax; toggleColor() }

    if (y > yMax)      { y = yMin; toggleColor() }
    else if (y < yMin) { y = yMax; toggleColor() }

    Canvas.line(x, y, x + dx, y + dy)

    x += dx
    y += dy
  }

  def loopUntilClosed(): Unit = while (!isClosed) {
    val t0 = now
    Canvas.awaitEvent(maxWaitForEventMillis)

    handleEvent()
    updateState()

    val elapsed = elapsedMillis(since = t0)
    val delayMillis = millisPerFrame - elapsed

    if (elapsed > millisPerFrame)
      print(s" lag ${((elapsed - millisPerFrame)*100).round/100.0} ms ")
    if (delayMillis > 0) Thread.sleep(delayMillis.round.toLong)
  }

  def main(args: Array[String]): Unit = {
    println(s"main started with args=$args")
    init()
    loopUntilClosed()

    /* Demo new canvas window: */
    val w = new Canvas("Extra",(640,400),Color.Black,false)
    w.writeText("Waiting for this window to close...",10,200)
    do w.awaitEvent(1000) while (w.lastEventType != Event.WindowClosed)

    Canvas.stopAllWindows
  }
}
