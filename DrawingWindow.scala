trait DrawingWindowApi {
  trait Event
  case class  Key(code: javafx.scene.input.KeyCode) extends Event
  case class  Click(x: Double, y: Double)           extends Event
  case object Closed                                extends Event

  case class Color(color: String){
    def toFxColor: javafx.scene.paint.Color = javafx.scene.paint.Color.web(color)
  }
  object Color {
    object Black extends Color("black")
    object White extends Color("white")
  }

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit

  def rect(x: Double, y: Double, width: Double, height: Double): Unit

  def fillRect(x: Double, y: Double, width: Double, height: Double): Unit

  def canvasSize: (Int, Int)

  def clearCanvas(): Unit

  def write(text: String, x: Int, y: Int): Unit

  def getColor: Color

  def setColor(color: Color): Unit

  def getFillColor: Color

  def setFillColor(color: Color): Unit

  def blockUntilEvent: Event

  def waitForEvent(timeout: Int): Option[Event]

  def withGraphics(callback: javafx.scene.canvas.GraphicsContext => Unit): Unit

  def withStage(callback: javafx.stage.Stage => Unit): Unit
}
