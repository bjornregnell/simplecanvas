/** A wrapper with utils for simpler access to javafx */
object Fx {
  @volatile private var _theApp: MinimalFxApp = _
  def theApp: FxApp = _theApp

  def runInNewThread(block: => Unit): Unit =
    new Thread( new Runnable() { override def run(): Unit = block }).start()

  @volatile private var delayedAppInit: javafx.stage.Stage => Unit = _
  @volatile private var _isStarted = false
  def isStarted = _isStarted

  private def launchApp(initPrimaryStage: javafx.stage.Stage => Unit): Unit = {
    runInNewThread {
      delayedAppInit = s => { initPrimaryStage(s); _isStarted = true }
      javafx.application.Application.launch(classOf[MinimalFxApp])
    }
    // Below ugly polling solves error: "Toolkit not initialized on line 28"
    while (!isStarted) { Thread.sleep(10) }
 }

  def runInFxThread(block: => Unit): Unit = javafx.application.Platform.runLater {
    new Runnable() { override def run(): Unit = block }
  }

  def newWindow(init: javafx.stage.Stage => Unit): Unit =
    if (!isStarted) launchApp(init)
    else runInFxThread{ init(new javafx.stage.Stage) }

  trait FxApp { def primaryStage: javafx.stage.Stage }

  private class MinimalFxApp extends javafx.application.Application with FxApp {
    private var _primaryStage: javafx.stage.Stage = null
    def primaryStage: javafx.stage.Stage = _primaryStage

    override def start(primaryStage: javafx.stage.Stage): Unit = {
      _primaryStage = primaryStage
      _theApp = this
      delayedAppInit(primaryStage)
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
