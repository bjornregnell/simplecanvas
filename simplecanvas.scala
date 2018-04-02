package simplecanvas
import simplefx._

case class Color(red: Int, green: Int, blue: Int, opacity: Double){
  require(Seq(red, green, blue).forall(c => Range(0,256).contains(c)))
  def fxColor: javafx.scene.paint.Color = ???
}
object Color {
  val BLACK = Color(javafx.scene.paint.Color.BLACK)
  val WHITE = Color(javafx.scene.paint.Color.WHITE)
  val RED   = Color(javafx.scene.paint.Color.RED)
  val GREEN = Color(javafx.scene.paint.Color.GREEN)
  val BLUE  = Color(javafx.scene.paint.Color.BLUE)
  def apply(c: javafx.scene.paint.Color) = {
    def rgb(c: Double): Int = (c * 256).toInt
    new Color(rgb(c.getRed), rgb(c.getGreen), rgb(c.getBlue), c.getOpacity)
  }
}

/** A graphics api with keyboard and mouse events for beginner programmers. */
trait SimpleCanvas {
  /** Draw line from (x1, y1) to (x2, y2) using the current lineWidth and lineColor */
  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit

  /** */
  def setLineColor(c: Color): Unit

  /** Fill a rectangle at (x, y) to (x2, y2) using current width and color */
  def fill(x1: Double, y1: Double, x2: Double, y2: Double): Unit

  /** */
  def setFillColor(c: Color): Unit

  /** */
  def writeText(text: String, x: Double, y: Double): Unit

  /** */
  def size: (Double, Double)

  /** */
  def delay(millis: Long): Unit

  /** */
  def clear(): Unit

  /** */
  object Event {
    val KeyPressed    = 0
    val KeyReleased   = 1
    val MousePressed  = 2
    val MouseReleased = 3
    val WindowClosed  = 4
    val Undefined     = -1
  }

  /** */
  def hasNextEvent: Boolean

  /** */
  def blockUntilEvent(): Unit

  /** */
  def waitForEvent(timeoutInMillis: Long): Unit

  /** */
  def lastEventType: Int

  /** */
  def lastKeyCode: javafx.scene.input.KeyCode

  /** */
  def lastKeyText: String

  /** Enable advanced graphics via underlying JavaFX Canvas */
  def getGraphicsContext: javafx.scene.canvas.GraphicsContext
}

/** A module ready to use in the Scala REPL or in a main Scala program */
object Canvas extends CanvasWindow

/** Implements the SimpleCanvasApi using JavaFX via the Fx layer */
class CanvasWindow(
  val initTitle: String = "Canvas",
  val initSize: (Int, Int) = (800, 640),
  val background: javafx.scene.paint.Color = javafx.scene.paint.Color.BLACK,
  val showBasicMenu: Boolean = true
) extends SimpleCanvas {

  protected val eventQueueCapacity = 1000
  protected var eventQueue =
    new java.util.concurrent.LinkedBlockingQueue[javafx.event.Event](eventQueueCapacity)

  protected var _lastEventType = Event.Undefined
  def lastEventType: Int = _lastEventType

  protected var _lastKeyCode: javafx.scene.input.KeyCode = javafx.scene.input.KeyCode.UNDEFINED
  def lastKeyCode: javafx.scene.input.KeyCode = _lastKeyCode

  protected var _lastKeyText = ""
  def lastKeyText: String = _lastKeyText

  protected var _lastMousePos = (0.0, 0.0)
  def lastMousePos: (Double, Double) = _lastMousePos

  def hasNextEvent: Boolean = eventQueue.size > 0

  protected def handleEvent(e: javafx.event.Event): Unit = e match {
    case ke: javafx.scene.input.KeyEvent =>
      _lastKeyCode = ke.getCode
      _lastKeyText = ke.getText
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

    case e => _lastEventType = Event.Undefined
  }

  def blockUntilEvent(): Unit = handleEvent(eventQueue.take)

  def waitForEvent(timeoutInMillis: Long): Unit = {
    val e = eventQueue.poll(timeoutInMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    if (e != null) handleEvent(e)
  }

  def delay(millis: Long): Unit = Thread.sleep(millis)

  def isFullScreen: Boolean = stage.isFullScreen
  def setFullScreen(isFull: Boolean): Unit = Fx.runInFxThread{ stage.setFullScreen(isFull) }

  protected val canvas = new javafx.scene.canvas.Canvas(initSize._1, initSize._2)
  protected val root = new javafx.scene.layout.VBox
  protected val scene = new javafx.scene.Scene(root, initSize._1, initSize._2, background)
  protected def gc = canvas.getGraphicsContext2D
  def getGraphicsContext = gc

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit =  gc.strokeLine(x1,y1,x2,y2)
  def setLineColor(c: Color): Unit = gc.setStroke(c.fxColor)

  def fill(x1: Double, y1: Double, x2: Double, y2: Double): Unit =  gc.fillRect(x1,y1,x2,y2)
  def setFillColor(c: Color): Unit = gc.setFill(c.fxColor)

  def writeText(text: String, x: Double, y: Double): Unit = gc.strokeText(text, x, y)

  def size: (Double, Double) = (stage.getWidth, stage.getHeight)
  def clear(): Unit = gc.clearRect(0, 0, size._1, size._2)

  protected lazy val basicMenuBar =
    Fx.menuBar(
      Fx.menu("File", Fx.menuItem("Quit", "Ctrl+Q", () => Fx.stop)),
      Fx.menu("View", Fx.menuItem("Toggle Full Screen", "F11",
        () => stage.setFullScreen(!stage.isFullScreen))
      )
    )

  protected val stage: javafx.stage.Stage = Fx.mkStage { s =>
      s.show
      s.setTitle(initTitle)
      root.setBackground(javafx.scene.layout.Background.EMPTY)
      gc.setStroke(background.invert)
      s.setScene(scene)
      root.getChildren.add(canvas)
      if (showBasicMenu) root.getChildren.add(0, basicMenuBar)
      scene.setOnKeyPressed   (e => {Fx.debug(e); eventQueue.offer(e)})
      scene.setOnKeyReleased  (e => {Fx.debug(e); eventQueue.offer(e)})
      scene.setOnMousePressed (e => {Fx.debug(e); eventQueue.offer(e)})
      scene.setOnMouseReleased(e => {Fx.debug(e); eventQueue.offer(e)})
  }
}
