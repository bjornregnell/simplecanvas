package simplecanvas

object Example {

  import simplecanvas._

  def main(args: Array[String]): Unit = {
    println(s"main started with args=$args")
    Canvas.line(0,0,1000,1000)
    /*
    val cw = new CanvasWindow("A second window.")
    cw.line(100,100,200,100)

    val ws = (3 to 5).map{ i => new CanvasWindow(s"Yet another window $i")}
    for ((w,i) <- ws.zipWithIndex) w.line(0,0,(i+1)*100,(i+1)*200)
    */

    Canvas.focus
    import Canvas._
    import javafx.scene.input.KeyCode
    var x = 100.0
    var y = 100.0
    val step = 10
    var dy = 0
    var dx = 0
    println("Starting game loop.")
    def now  = System.nanoTime / 1000L
    val loopMillis = 18
    val maxWaitMillis = 2
    var startTime = now

    var colorIndex = 0
    val colvec = Vector(Color.Red, Color.Green, Color.Blue)
    def toggleColor(): Unit = {
      colorIndex = (colorIndex + 1) % colvec.size
      setLineColor(colvec(colorIndex))
    }

    setLineWidth(20)

    val (xMax, yMax) = (500, 500)
    val (xMin, yMin) = (100, 100)
    var quit = false
    while (!quit) {
      do {
        waitForEvent(maxWaitMillis)
        if (lastEventType == Event.KeyPressed) {
          if (lastKeyCode == KeyCode.DOWN)  { dx = 0;     dy = step  }
          if (lastKeyCode == KeyCode.UP)    { dx = 0;     dy = -step }
          if (lastKeyCode == KeyCode.RIGHT) { dx = step;  dy = 0     }
          if (lastKeyCode == KeyCode.LEFT)  { dx = -step; dy = 0     }
          toggleColor()
        }
        else if (lastEventType == Event.WindowHiding) {
          quit = true
        }
        if (lastEventType != Event.Undefined)
         println(s"""lastEventType: $lastEventType lastKeyCode: "$lastKeyCode" lastMousePos $lastMousePos (x,y)=($x,$y) (dx,dy)=($dx,$dy) size=$size """)
        //else print(".")
      } while (hasNextEvent)

      if (x > xMax)      { x = xMin; toggleColor() }
      else if (x < xMin) { x = xMax; toggleColor() }

      if (y > yMax)      { y = yMin; toggleColor() }
      else if (y < yMin) { y = yMax; toggleColor() }

      line(x, y, x + dx, y + dy)

      x += dx
      y += dy

      val elapsed = now - startTime
      //if (elapsed > 0) print(s" $elapsed ")
      val delayMillis = loopMillis - elapsed/1000
      if (delayMillis > 0) delay(delayMillis)
      startTime = now
    }
  }
}
