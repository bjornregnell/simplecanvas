class CanvasWindow(
  val initTitle: String = "Canvas",
  val initSize: (Int, Int) = (800, 640),
  val background: javafx.scene.paint.Color = javafx.scene.paint.Color.BLACK,
  val hasAppMenu: Boolean = true
) {

  var eventQ = new java.util.concurrent.LinkedBlockingQueue[javafx.event.Event]

  private var _lastEventType = CanvasEvent.EventTypeUndefined
  def lastEventType: Int = _lastEventType

  private var _lastKeyCode: javafx.scene.input.KeyCode = javafx.scene.input.KeyCode.UNDEFINED
  def lastKeyCode: javafx.scene.input.KeyCode = _lastKeyCode

  private var _lastKeyText: String = ""
  def lastKeyText: String = _lastKeyText

  def isNextEvent: Boolean = eventQ.size > 0

  def blockUntilEvent(): Unit = eventQ.take match {
    case ke: javafx.scene.input.KeyEvent =>
      _lastKeyCode = ke.getCode
      _lastKeyText = ke.getText
      ke.getEventType match {
        case javafx.scene.input.KeyEvent.KEY_PRESSED => _lastEventType = CanvasEvent.KeyPressed
        case javafx.scene.input.KeyEvent.KEY_RELEASED => _lastEventType = CanvasEvent.KeyReleased
        case _ => _lastEventType = CanvasEvent.EventTypeUndefined
      }

    case e => _lastEventType = CanvasEvent.EventTypeUndefined
  }

  def withStage(callback: javafx.stage.Stage => Unit): Unit =
    Fx.runInFxThread { callback(stage) }

  def isFullScreen = stage.isFullScreen
  def setFullScreen(isFull: Boolean): Unit = Fx.runInFxThread{ stage.setFullScreen(isFull) }

  /*protected*/ val canvas = new javafx.scene.canvas.Canvas(initSize._1, initSize._2)
  /*protected*/ val root = new javafx.scene.layout.VBox
  /*protected*/ val scene = new javafx.scene.Scene(root, initSize._1, initSize._2, background)
  /*protected*/ val gc = canvas.getGraphicsContext2D
  def getGraphics = gc

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit =  gc.strokeLine(x1,y1,x2,y2)

  def line(p1: (Double, Double))(p2: (Double, Double)): Unit =
    gc.strokeLine(p1._1, p1._2, p2._1, p2._2)

  def rect(x: Int, y: Int, width: Int, height: Int): Unit = gc.strokeRect(x, y, width, height)

  def rect(p:(Int, Int))(dxy: (Int, Int)): Unit = gc.strokeRect(p._1, p._2, dxy._1, dxy._2)

  def writeText(text: String, x: Double, y: Double): Unit = gc.strokeText(text, x, y)

  def setTitle(newTitle: String): Unit = withStage { _.setTitle(newTitle) }
  def size: (Double, Double) = (stage.getWidth, stage.getHeight)
  def clear(): Unit = gc.clearRect(0, 0, size._1, size._2)


  /*protected*/ lazy val appMenuBar =
                      Fx.menuBar(
                        Fx.menu("File",
                          Fx.menuItem("Quit", "Ctrl+Q", () => Fx.stop)
                        ),
                        Fx.menu("View",
                          Fx.menuItem("Toggle Full Screen", "F11", () => stage.setFullScreen(!stage.isFullScreen))
                        )
                      )

  /*protected*/ val stage: javafx.stage.Stage = Fx.mkStage { s =>
      s.show
      s.setTitle(initTitle)
      root.setBackground(javafx.scene.layout.Background.EMPTY)
      gc.setStroke(background.invert)
      s.setScene(scene)
      root.getChildren.add(canvas)
      if (hasAppMenu) root.getChildren.add(0, appMenuBar)
      scene.setOnKeyPressed (ke => { println(s"\nkey $ke"); eventQ.offer(ke) })
      scene.setOnKeyReleased(ke => { println(s"\nkey $ke"); eventQ.offer(ke) })
      scene.setOnMousePressed (ke => { println(s"\nkey $ke"); eventQ.offer(ke) })
      scene.setOnMouseReleased(ke => { println(s"\nkey $ke"); eventQ.offer(ke) })
  }
}

object Canvas extends CanvasWindow

object CanvasEvent {
  val EventTypeUndefined = 0
  val KeyPressed = 1
  val KeyReleased = 2
  val MousePressed = 3
  val MouseReleased = 4
}
