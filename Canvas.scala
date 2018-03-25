class CanvasWindow(title: String = "Canvas", size: (Int, Int) = (800, 640)) {

  type Callback = () => Unit
  var onOpen: Callback = () => println("Open menu selected")
  var onSave: Callback = () => println("Save menu selected")
  var onQuit: Callback = () => Fx.stop
  var onFullScreen: Callback = () => println(s"isFullScreen == $isFullScreen")

  private var _isInitialized = false
  private var _stage: javafx.stage.Stage = _

  def withStage(callback: javafx.stage.Stage => Unit): Unit =
    Fx.runInFxThread { callback(_stage) }

  def isFullScreen = _isInitialized && _stage.isFullScreen

  private var _canvas: javafx.scene.canvas.Canvas = _

  def withGraphics(callback: javafx.scene.canvas.GraphicsContext => Unit) =
    Fx.runInFxThread{ callback(_canvas.getGraphicsContext2D) }

  def line(p1: (Double, Double), p2: (Double, Double)): Unit =  withGraphics { gc =>
    gc.strokeLine(p1._1, p1._2, p2._1, p2._2)
  }

  def rect(p:(Int, Int), dxy: (Int, Int)): Unit = withGraphics { gc =>
    gc.fillRect(p._1, p._2, dxy._1, dxy._2)
  }

  Fx.newWindow { stage =>
      stage.setTitle(title)
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
              stage.setFullScreen(!isFullScreen)
              onFullScreen()
            })
          )
        ),
        _canvas
      )
      stage.setScene(new javafx.scene.Scene(root, size._1, size._2))
      stage.setResizable(false)
      stage.show
      _stage = stage
      _isInitialized = true
  }
}

object Canvas extends CanvasWindow
