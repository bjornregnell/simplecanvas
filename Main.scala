object Main {
  import SimpleCanvas._

  def main(args: Array[String]): Unit = {
    println("Launching application...")

    line((10,10),(100,100))

    rect((200,200), (30, 70))
    title("Första Simpla Canvas-fönstret")

    // Fx.newWindow { stage =>
    //   stage.setTitle("nytt fönster")
    //   val root = new javafx.scene.layout.VBox
    //   stage.setScene(new javafx.scene.Scene(root, 500, 500))
    //   stage.setResizable(false)
    //   root.getChildren.addAll(Fx.canvas(500,500))
    //   stage.show
    // }

    val cw = new CanvasWindow("ännu ett fönster")
    cw.rect((200,200), (30, 70))
    val ws = (3 to 10).map{ i => new CanvasWindow(s"fönster $i")}
  }
}
