class CanvasWindow(
  val initTitle: String = "Canvas",
  val initSize: (Int, Int) = (800, 640),
  val background: javafx.scene.paint.Color = javafx.scene.paint.Color.BLACK
) {

  type Callback = () => Unit
  var onOpen: Callback = () => println("Open menu selected")
  var onSave: Callback = () => println("Save menu selected")
  var onQuit: Callback = () => Fx.stop
  var onFullScreen: Callback = () => println(s"isFullScreen == $isFullScreen")

  def withStage(callback: javafx.stage.Stage => Unit): Unit =
    Fx.runInFxThread { callback(stage) }

  def isFullScreen = stage.isFullScreen
  def setFullScreen(isFull: Boolean): Unit = Fx.runInFxThread{ stage.setFullScreen(isFull) }

  protected var _canvas: javafx.scene.canvas.Canvas = _

  def withGraphics(callback: javafx.scene.canvas.GraphicsContext => Unit) =
    Fx.runInFxThread{ callback(_canvas.getGraphicsContext2D) }

  def line(p1: (Double, Double), p2: (Double, Double)): Unit =  withGraphics { gc =>
    gc.strokeLine(p1._1, p1._2, p2._1, p2._2)
  }

  def rect(p:(Int, Int), dxy: (Int, Int)): Unit = withGraphics { gc =>
    gc.fillRect(p._1, p._2, dxy._1, dxy._2)
  }

  def setTitle(newTitle: String): Unit = withStage { _.setTitle(newTitle) }
  def getSize: (Double, Double) = (stage.getWidth, stage.getHeight)

  protected val stage: javafx.stage.Stage = Fx.mkStage { s =>
      s.show
      s.setTitle(initTitle)
      val root = new javafx.scene.layout.VBox
      _canvas = new javafx.scene.canvas.Canvas(initSize._1, initSize._2)
      _canvas.getGraphicsContext2D.setStroke(background.invert)
      s.setScene(new javafx.scene.Scene(root, initSize._1, initSize._2, background))
      root.getChildren.addAll(
        _canvas
      /*  Fx.menuBar(
          Fx.menu("File",
            Fx.menuItem("Open...", "Ctrl+O", onOpen),
            Fx.menuItem("Save...", "Ctrl+S", onSave),
            Fx.menuItem("Quit",    "Ctrl+Q", onQuit)
          ),
          Fx.menu("View",
            Fx.menuItem("Toggle Full Screen", "F11", () => {
              stage.setFullScreen(!stage.isFullScreen)
              onFullScreen()
            })
          )
        ),*/
      )
      //s.setResizable(false)
  }
}

object Canvas extends CanvasWindow
