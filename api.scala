package simplecanvas

/** Types of events that can happen in a canvas window */
object Event {
  val KeyPressed    = "KeyPressed"
  val KeyReleased   = "KeyReleased"
  val MousePressed  = "MousePressed"
  val MouseReleased = "MouseReleased"
  val WindowClosed  = "WindowClosed"

  /** Used to indicate that no event is available */
  val Undefined     = "Undefined"
}

/* Predefined colors. */
object Color {
  val Black = Color(0,0,0)
  val White = Color(255, 255,255)
  val Red   = Color(255, 0, 0)
  val Green = Color(0, 255, 0)
  val Blue  = Color(0, 0, 255)
}

/* Custom rgb color with alpha controlling opacity. */
case class Color(red: Int, green: Int, blue: Int, alpha: Int = 255){
  require(0 to 255 contains red,   s"red=$red must be within 0 to 255")
  require(0 to 255 contains green, s"green=$green must be within 0 to 255")
  require(0 to 255 contains blue,  s"blue=$blue must be within 0 to 255")
  require(0 to 255 contains alpha, s"alpha=$alpha must be within 0 to 255")
}

/** A graphics api with keyboard and mouse events for beginner programmers. */
trait SimpleCanvas {
  /** Initial title of window */
  val initTitle: String

  /** Initial size of window (width, height) */
  val initSize: (Int, Int)

  /** Initial background of window */
  val initBackground: Color

  /** Initializes the application menu with basig items if true */
  val initBasicMenu: Boolean

  /** Draw line from (x1, y1) to (x2, y2) using the current lineWidth and lineColor */
  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit

  def setLineWidth(width: Double): Unit

  /** */
  def setLineColor(c: Color): Unit

  /** Draw a rectangle at (x, y) of width w, height h using current lineWidth and lineColor */
  def rect(x: Double, y: Double, w: Double, h: Double): Unit

  /** Fill a rectangle at (x, y) of width w, height h using current fillColor */
  def fillRect(x: Double, y: Double, w: Double, h: Double): Unit

  /** */
  def setFillColor(c: Color): Unit

  /** */
  def writeText(text: String, x: Double, y: Double): Unit

  /** */
  def size: (Double, Double)

  /** */
  def clear(): Unit

  /** */
  def show(): Unit

  /** */
  def awaitEvent(timeoutInMillis: Int): Unit

  /** */
  def lastEventType: String

  /** */
  def lastKeyCode: String

  /** */
  def lastKeyText: String  // ??? is this realy needed?

  def lastMousePos: (Double, Double)

  def isFullScreen: Boolean

  def setFullScreen(isFull: Boolean): Unit
}

trait GlobalControl {
  /** Exit application */
  def systemExit(): Unit

  /** Stop and close all windows */
  def stopAllWindows(): Unit

  /** Delay current thread for millis milliseconds*/
  def delay(millis: Int): Unit
}
