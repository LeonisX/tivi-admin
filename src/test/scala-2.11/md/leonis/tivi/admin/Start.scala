package md.leonis.tivi.admin

import java.util.{Timer, TimerTask}
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}


object Start {
  def main(args: Array[String]) {
    Application.launch(classOf[Start], args: _*)
  }
}

class Start extends Application {
  val canvas = new Canvas(1600, 600)
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

  def drawShapes(process: MySqlProcess, color: Color) {
    val gc = canvas.getGraphicsContext2D

    gc.setLineWidth(0.5)
    gc.setStroke(Color.GRAY)
    gc.strokeLine(50.5, canvas.getHeight - 50.5, canvas.getWidth, canvas.getHeight - 50.5)
    gc.strokeLine(50.5, canvas.getHeight - 50.5, 50.5, 0)

    gc.setStroke(color)
    gc.translate(50.5,  - 50.5)
    //gc.getPixelWriter.setColor(ds.prev._1.toInt, (canvas.getHeight - 50 - ds.prev._2 * 1000).toInt, color)
    val m = process.threshold * ((1001 - process.threshold) + 0.000000000000000001)
    gc.strokeLine(process.prev._1 / m, canvas.getHeight - process.prev._2 * 500, process.next._1 / m, canvas.getHeight - process.next._2 * 500)
    gc.translate(-50.5, 50.5)
    gc.clearRect(0,0,300,100)
    gc.fillText(process.prev._1.toString, 50, 50)
  }
}
