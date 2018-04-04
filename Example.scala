package simplecanvas
import simplecanvas._
import javafx.scene.input.KeyCode

object Example {

  object State {
    val xMax = 600
    val yMax = 400
    val xMin = 100
    val yMin = 100

    val millisPerFrame = 15
    val maxWaitForEventMillis = 2

    var x = 100.0
    var y = 100.0
    var step = 10
    var dy = 0
    var dx = 0

    val colvec = Vector(Color.Red, Color.Green, Color.Blue)
    var colorIndex = 0
  }

  import State._

  def toggleColor(): Unit = {
    colorIndex = (colorIndex + 1) % colvec.size
    Canvas.setLineColor(colvec(colorIndex))
  }

  def now = System.nanoTime
  def elapsedMillis(since: Long): Long = (now - since) / 1000000L

  def init(): Unit = {
    Canvas.focus
    Canvas.setLineWidth(20)
    toggleColor()
  }

  def handleEvent(): Unit = {
    import Canvas._
    if (lastEventType == Event.KeyPressed) {
      if (lastKeyCode == KeyCode.DOWN)  { dx = 0;     dy = step  }
      if (lastKeyCode == KeyCode.UP)    { dx = 0;     dy = -step }
      if (lastKeyCode == KeyCode.RIGHT) { dx = step;  dy = 0     }
      if (lastKeyCode == KeyCode.LEFT)  { dx = -step; dy = 0     }
      if (lastKeyCode == KeyCode.F11) setFullScreen(!isFullScreen)
      toggleColor()
    } else if (lastEventType != Event.Undefined) {
     println(s"""
       lastEventType: $lastEventType
       lastKeyCode:  "$lastKeyCode"
       lastMousePos $lastMousePos
       (x,y)=($x,$y) (dx,dy)=($dx,$dy) size=$size """)
    } else print(".")
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

  def loopForever(): Unit = while (true) {
    val t0 = now
    Canvas.waitForEvent(maxWaitForEventMillis)

    handleEvent()
    updateState()

    val elapsed = elapsedMillis(since = t0)
    val delayMillis = millisPerFrame - elapsed

    if (elapsed > 0) print(s" $elapsed ")
    if (delayMillis > 0) Thread.sleep(delayMillis)
  }

  def main(args: Array[String]): Unit = {
    println(s"main started with args=$args")
    init()
    loopForever()
  }
}
