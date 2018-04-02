package simplefx

/** A wrapper with utils for simpler access to javafx */
object Fx {
  @volatile var isDebug = true
  def debug[T](a: T) = if (isDebug) println(a.toString)

  @volatile private var _primaryStage: javafx.stage.Stage = _
  def primaryStage: javafx.stage.Stage = _primaryStage

  @volatile private var delayedAppInit: javafx.stage.Stage => Unit = _

  private val signalFxStarted = new java.util.concurrent.CountDownLatch(1)
  def isStarted: Boolean = signalFxStarted.getCount() == 0

  private def launchApp(initPrimaryStage: javafx.stage.Stage => Unit): Unit = {
    val t0 = System.nanoTime
    delayedAppInit = initPrimaryStage  // only assigned once here
    new Thread( () => {
      javafx.application.Application.launch(classOf[UnderlyingApp]) // blocks until exit
    }).start
    signalFxStarted.await
    debug(s"JavaFX Toolkit launched in ${(System.nanoTime - t0)/1000000} ms")
  }

  def runInFxThread(block: => Unit): Unit =
    javafx.application.Platform.runLater { () => block }

  /** Creates a new window and at first call launches the application. */
  def mkStage(init: javafx.stage.Stage => Unit): javafx.stage.Stage =
    if (!isStarted) {
      launchApp(init)
      primaryStage
    } else {
      val ready = new java.util.concurrent.CountDownLatch(1)
      var nonPrimaryStage: javafx.stage.Stage = null
      runInFxThread {
        nonPrimaryStage = new javafx.stage.Stage;
        init(nonPrimaryStage)
        ready.countDown
      }
      ready.await
      nonPrimaryStage
    }

  private class UnderlyingApp extends javafx.application.Application {
    override def start(primaryStage: javafx.stage.Stage): Unit = {
      _primaryStage = primaryStage  // only assigned once here
      delayedAppInit(primaryStage)  // only called once here
      signalFxStarted.countDown
    }
    override def stop(): Unit = {
      debug("JavaFX Toolkit Application stopped.")
    }
  }

  def menuItem(
    name: String,
    shortcut: String = "",
    action: () => Unit
  ): javafx.scene.control.MenuItem = {
    val item = javafx.scene.control.MenuItemBuilder.create()
      .text(name)
      .onAction(e => action())
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

  def menuBar(items: javafx.scene.control.Menu*) = new javafx.scene.control.MenuBar(items:_*)

  def stop(): Unit = javafx.application.Platform.exit
}
