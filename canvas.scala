package simplecanvas
import wrapfx._

/** A module ready to use in the Scala REPL or in a main Scala program */
object Canvas extends CanvasWindow(
  initTitle      = "Canvas",
  initSize       = (1000,1000),
  initBackground = Color.Black,
  initBasicMenu  = true
) with GlobalControl {
  override def systemExit(): Unit = System.exit(0)

  override def stopAllWindows(): Unit= Fx.stop()

  override def delay(millis: Int): Unit = Thread.sleep(millis)
}

/** Implements the SimpleCanvas api using javafx via the wrapfx layer */
class CanvasWindow(
  override val initTitle: String      = "Another Canvas Window",
  override val initSize: (Int, Int)   = (1000,1000),
  override val initBackground: Color  = Color.Black,
  override val initBasicMenu: Boolean = false,
) extends SimpleCanvas {

  protected val eventQueueCapacity = 1000
  protected var eventQueue =
    new java.util.concurrent.LinkedBlockingQueue[javafx.event.Event](eventQueueCapacity)

  protected var _lastEventType = Event.Undefined
  override def lastEventType: String = _lastEventType

  protected var _lastKeyCode: javafx.scene.input.KeyCode = javafx.scene.input.KeyCode.UNDEFINED
  override def lastKeyCode: String =
    if (_lastKeyCode == javafx.scene.input.KeyCode.UNDEFINED && _lastKeyText != "")
      _lastKeyText.toUpperCase // This makes åäö show as ÅÄÖ and not UNDEFINED
    else _lastKeyCode.toString

  protected var _lastKeyText = ""
  override def lastKeyText: String = _lastKeyText

  protected var _lastMousePos = (0.0, 0.0)
  override def lastMousePos: (Double, Double) = _lastMousePos

  protected def handleEvent(e: javafx.event.Event): Unit = e match {
    case ke: javafx.scene.input.KeyEvent =>
      _lastKeyCode = ke.getCode
      _lastKeyText = ke.getText   // TODO Handle undefined...
      ke.getEventType match {
        case javafx.scene.input.KeyEvent.KEY_PRESSED => _lastEventType = Event.KeyPressed
        case javafx.scene.input.KeyEvent.KEY_RELEASED => _lastEventType = Event.KeyReleased
        case _ => _lastEventType = Event.Undefined
      }

    case me: javafx.scene.input.MouseEvent =>
      _lastMousePos = (me.getX, me.getY)
      me.getEventType match {
        case javafx.scene.input.MouseEvent.MOUSE_PRESSED => _lastEventType = Event.MousePressed
        case javafx.scene.input.MouseEvent.MOUSE_RELEASED => _lastEventType = Event.MouseReleased
        case _ => _lastEventType = Event.Undefined
      }

    case we: javafx.stage.WindowEvent
      if we.getEventType == javafx.stage.WindowEvent.WINDOW_HIDING =>
        _lastEventType = Event.WindowClosed

    case e => _lastEventType = Event.Undefined
  }

  override def awaitEvent(timeoutInMillis: Int): Unit = {
    val e = eventQueue.poll(timeoutInMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    if (e != null) handleEvent(e)
    else _lastEventType = Event.Undefined
  }

  override def isFullScreen: Boolean = stage.isFullScreen
  override def setFullScreen(isFull: Boolean): Unit = Fx(stage.setFullScreen(isFull))

  protected def fxColor(c: Color): javafx.scene.paint.Color =
    javafx.scene.paint.Color.rgb(c.red, c.green, c.blue, c.alpha / 255.0)

  protected val canvas = new javafx.scene.canvas.Canvas(initSize._1, initSize._2)
  protected val root = new javafx.scene.layout.VBox
  protected val scene =
    new javafx.scene.Scene(root, initSize._1, initSize._2, fxColor(initBackground))

  protected def withGC(callback: javafx.scene.canvas.GraphicsContext => Unit): Unit =
    Fx(callback(canvas.getGraphicsContext2D))

  override def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit =  withGC(_.strokeLine(x1,y1,x2,y2))
  override def setLineWidth(width: Double): Unit = withGC(_.setLineWidth(width))
  override def setLineColor(c: Color): Unit = withGC(_.setStroke(fxColor(c)))

  override def setFillColor(c: Color): Unit = withGC(_.setFill(fxColor(c)))
  override def rect(x: Double, y: Double, w: Double, h: Double): Unit =  withGC { _.strokeRect(x, y , w, h) }

  override def fillRect(x1: Double, y1: Double, x2: Double, y2: Double): Unit =  withGC(_.fillRect(x1,y1,x2,y2))

  override def writeText(text: String, x: Double, y: Double): Unit = withGC(_.strokeText(text, x, y))

  override def size: (Double, Double) = (stage.getWidth, stage.getHeight)
  override def clear(): Unit = withGC(_.clearRect(0, 0, size._1, size._2))

//  override def hideAndStop():  Unit = Fx(stage.hide)  TODO should this be expose???
  override def show():  Unit = Fx{stage.show; stage.requestFocus}

  protected val basicMenuBar =
    Fx.menuBar(
      Fx.menu("File", Fx.menuItem("Quit", "Ctrl+Q", () => System.exit(0))),
      Fx.menu("View", Fx.menuItem("Toggle Full Screen", "F11",
        () => stage.setFullScreen(!stage.isFullScreen))
      )
    )

  protected val stage: javafx.stage.Stage = Fx.mkStage { s =>
      s.show
      s.setTitle(initTitle)
      root.setBackground(javafx.scene.layout.Background.EMPTY)
      canvas.getGraphicsContext2D.setStroke(fxColor(initBackground).invert)
      s.setScene(scene)
      root.getChildren.add(canvas)
      if (initBasicMenu) root.getChildren.add(0, basicMenuBar)
      canvas.setOnKeyPressed   (e => { Fx.debug(e); eventQueue.offer(e)} )
      canvas.setOnKeyReleased  (e => { Fx.debug(e); eventQueue.offer(e)} )
      canvas.setOnMousePressed (e => { Fx.debug(e); eventQueue.offer(e)} )
      canvas.setOnMouseReleased(e => { Fx.debug(e); eventQueue.offer(e)} )
      s.setOnHiding( e =>  { Fx.debug(e); eventQueue.clear(); eventQueue.offer(e) })
  }
}
