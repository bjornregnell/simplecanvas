/** A wrapper with utils for simpler access to javafx */
object Fx {
  @volatile private var _theApp: SimpleFxApp = _
  def theApp: SimpleFxApp = _theApp

  def runInNewThread(block: => Unit): Unit =
    new Thread( new Runnable() { override def run(): Unit = block }).start()

  @volatile private var doInit: () => Unit = _
  @volatile private var _isStarted = false
  def isStarted = _isStarted

  def start(initBlock: => Unit = {}): Unit = {
    runInNewThread {
      doInit = () => { initBlock; _isStarted = true }
      javafx.application.Application.launch(classOf[SimpleFxApp])
    }
    while (!isStarted) { Thread.sleep(100) }
  }

  def apply(block: => Unit): Unit = javafx.application.Platform.runLater {
    new Runnable() { override def run(): Unit = block }
  }

  class SimpleFxApp extends javafx.application.Application {
    private var _primaryStage: javafx.stage.Stage = null
    def primaryStage: javafx.stage.Stage = _primaryStage

    override def start(primaryStage: javafx.stage.Stage) {
      _primaryStage = primaryStage
      _theApp = this
      doInit()
    }
  }

  def eventHandler(action: () => Unit): javafx.event.EventHandler[javafx.event.ActionEvent] =
    new javafx.event.EventHandler[javafx.event.ActionEvent] {
      override def handle(e: javafx.event.ActionEvent): Unit = action()
    }

  def menuItem(
    name: String,
    shortcut: String = "",
    action: () => Unit
  ): javafx.scene.control.MenuItem = {
    val item = javafx.scene.control.MenuItemBuilder.create()
      .text(name)
      .onAction(eventHandler(action))
      .build()
    if (shortcut.nonEmpty) {
      item.setAccelerator(javafx.scene.input.KeyCombination.keyCombination(shortcut))
    }
    item
  }

  def menu(name: String, items: javafx.scene.control.MenuItem*): javafx.scene.control.Menu = {
    val menu = new javafx.scene.control.Menu(name)
    menu.getItems.addAll(items: _*)
    menu
  }

  def menuBar(items: javafx.scene.control.Menu*) =
    new javafx.scene.control.MenuBar(items:_*)

  def canvas(size: (Int, Int)) = new javafx.scene.canvas.Canvas(size._1, size._2)

  def stop(): Unit = javafx.application.Platform.exit


}
