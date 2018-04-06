package simplecanvas

object Event {
  val KeyPressed    = 0
  val KeyReleased   = 1
  val MousePressed  = 2
  val MouseReleased = 3
  val WindowClosed  = 4
  val Undefined     = -1
}

case class Color(red: Int, green: Int, blue: Int, opacity: Double = 1.0){
  require(Seq(red, green, blue).forall(i => Range(0,256).contains(i)))
  require(opacity >= 0.0 && opacity <= 1.0)
}
object Color {
  val Black = Color(0,0,0)
  val White = Color(255, 255,255)
  val Red   = Color(255, 0, 0)
  val Green = Color(0, 255, 0)
  val Blue  = Color(0, 0, 255)
}

/** A graphics api with keyboard and mouse events for beginner programmers. */
trait SimpleCanvas {
  /** Initial title of window */
  val initTitle: String      = "SimpleCanvas"

  /** Initial size of window (width, height) */
  val initSize: (Int, Int)   = (1000, 1000)

  /** Initial background of window */
  val initBackground: Color  = Color.Black

  /** Initializes the application menu with basig items if true */
  val initBasicMenu: Boolean = false

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
  def delay(millis: Long): Unit

  /** */
  def clear(): Unit

  /** */
  def show(): Unit

  /** */
//???  def hide(): Unit

  /** */
  def stopAllWindows(): Unit

  /** */
  def awaitEvent(timeoutInMillis: Long): Unit

  /** */
  def lastEventType: Int

  /** */
  def lastKeyCode: String

  /** */
  def lastKeyText: String  // ??? is this realy needed?

  def lastMousePos: (Double, Double)

  def isFullScreen: Boolean

  def setFullScreen(isFull: Boolean): Unit
}
