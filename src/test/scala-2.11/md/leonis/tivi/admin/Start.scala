package md.leonis.tivi.admin

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}


object Start {
  def main(args: Array[String]) {
    Application.launch(classOf[Start], args: _*)
  }
}

class Start extends Application {
  val canvas = new Canvas(1200, 600)
  var running = true

  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Drawing Operations Test")
    val root = new Group()
    val gc = canvas.getGraphicsContext2D
    //drawShapes(gc)
    root.getChildren.add(canvas)
    primaryStage.setScene(new Scene(root))

    primaryStage.setOnCloseRequest(new EventHandler[WindowEvent]() {
      def handle(we: WindowEvent) {
        running = false
      }
    })

    primaryStage.show()
    val f = this
    new Thread(new Runnable {
      def run() {
        Query.go(f)
      }
    }).start()

  }

  def drawShapes(ds: DS, color: Color) {
    val gc = canvas.getGraphicsContext2D
    gc.setStroke(color)
    gc.setLineWidth(0.5)
    //gc.getPixelWriter.setColor(ds.prev._1.toInt, (canvas.getHeight - 50 - ds.prev._2 * 1000).toInt, color)
    gc.strokeLine(ds.prev._1, canvas.getHeight - 50 - ds.prev._2 * 1000, ds.next._1, canvas.getHeight - 50 - ds.next._2 * 1000)
    gc.clearRect(0,0,300,100)
    gc.fillText((ds.prev._1 * 1000).toString, 50, 50)
  }
}
