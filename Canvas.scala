object Canvas {
  type Callback = () => Unit
  var onOpen: Callback = () => println("Open menu selected")
  var onSave: Callback = () => println("Save menu selected")
  var onQuit: Callback = () => Fx.stop
  var onFullScreen: Callback = () => println(s"isFullScreen == $isFullScreen")

  def isFullScreen = Fx.isStarted && Fx.theApp.primaryStage.isFullScreen

  private var _canvas: javafx.scene.canvas.Canvas = _
  def graphics: javafx.scene.canvas.GraphicsContext = _canvas.getGraphicsContext2D

  private def initIfNotStarted(): Unit = if (!Fx.isStarted) init()

  var pos = (0,0)

  def x = pos._1
  def x_=(newX: Int): Unit = { pos = (newX, y) }

  def y = pos._2
  def y_=(newY: Int): Unit = { pos = (x, newY) }

  def moveTo(newPos:(Int, Int)): Unit = { pos = newPos }

  def lineTo(newPos:(Int, Int)): Unit = {
    initIfNotStarted()
    graphics.strokeLine(x, y, newPos._1, newPos._2)
    pos = newPos
  }

  def init(title: String = "CanvasApp", size: (Int, Int) = (800, 640)): Unit =
    if (!Fx.isStarted) Fx.start {
      val root = new javafx.scene.layout.VBox
      _canvas = Fx.canvas(size)
      root.getChildren.addAll(
        Fx.menuBar(
          Fx.menu("File",
            Fx.menuItem("Open...", "Ctrl+O", onOpen),
            Fx.menuItem("Save...", "Ctrl+S", onSave),
            Fx.menuItem("Quit",    "Ctrl+Q", onQuit)
          ),
          Fx.menu("View",
            Fx.menuItem("Toggle Full Screen", "F11", () => {
              Fx.theApp.primaryStage.setFullScreen(!isFullScreen)
              onFullScreen()
            })
          )
        ),
        _canvas
      )
      Fx.theApp.primaryStage.setScene(new javafx.scene.Scene(root, size._1, size._2))
      Fx.theApp.primaryStage.setResizable(false)
      Fx.theApp.primaryStage.setTitle(title)
      Fx.theApp.primaryStage.show
    } else throw new Exception("Canvas.init must not be called more than once")
}
