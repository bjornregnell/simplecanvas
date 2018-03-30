/** A wrapper with utils for simpler access to javafx */
object Fx {
  @volatile private var _primaryStage: javafx.stage.Stage = _
  def primaryStage: javafx.stage.Stage = _primaryStage

  @volatile private var delayedAppInit: javafx.stage.Stage => Unit = _

  private val signalFxStarted = new java.util.concurrent.CountDownLatch(1)
  def isStarted = signalFxStarted.getCount() == 0

  private def launchApp(initPrimaryStage: javafx.stage.Stage => Unit): Unit = {
    delayedAppInit = initPrimaryStage  // only assigned once here
    new Thread( () => {
      javafx.application.Application.launch(classOf[ApplicationWindow]) // blocks until exit
    }).start
    signalFxStarted.await
 }

  def runInFxThread(block: => Unit): Unit =
    javafx.application.Platform.runLater { () => block }

  def mkStage(init: javafx.stage.Stage => Unit): javafx.stage.Stage =
    if (!isStarted) {
      launchApp(init)
      primaryStage
    } else {
      val ready = new java.util.concurrent.CountDownLatch(1)
      var stage: javafx.stage.Stage = null
      runInFxThread {
        stage = new javafx.stage.Stage;
        init(stage)
        ready.countDown
      }
      ready.await
      stage
    }

  private class ApplicationWindow extends javafx.application.Application {
    override def start(primaryStage: javafx.stage.Stage): Unit = {
      _primaryStage = primaryStage  // only assigned once here
      delayedAppInit(primaryStage)  // only called once here
      signalFxStarted.countDown
    }
    override def stop(): Unit = {
      println("Fx Stopped")
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
