package md.leonis.tivi.admin.test

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.{Group, Scene}
import javafx.stage.{Stage, WindowEvent}

object Start {
  def main(args: Array[String]) {
    Application.launch(classOf[Start], args: _*)
  }
}

class Start extends Application {
  val canvas = new Canvas(1700, 1000)
  var running = true

  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Drawing Operations Test")
    val root = new Group()
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

  def drawChart(divider: Int) {
    val gc = canvas.getGraphicsContext2D

    gc.setLineWidth(0.5)
    gc.setStroke(Color.GRAY)

    gc.translate(50.5, 0)
    gc.strokeLine(0, canvas.getHeight - 50.5, canvas.getWidth, canvas.getHeight - 50.5)
    gc.strokeLine(0, canvas.getHeight - 50.5, 0, 0)
    val font: Font = Font.font ("Dialog", 12)
    gc.setFont(font)
    val dashes = gc.getLineDashes
    gc.setLineDashes(2, 3)
    for (i <- 1 to 350) {
      gc.strokeLine(0, canvas.getHeight - 50.5 - i * 5, canvas.getWidth, canvas.getHeight - 50.5 - i * 5)
    }
    gc.setLineDashes(dashes:_*)
    for (i <- 1 to 35) {
      gc.strokeLine(i * 50, canvas.getHeight - 50.5, i * 50, 0)
      gc.fillText((i * 50 * divider / 1000).toString + "K", i * 50, canvas.getHeight - 30.5)
    }
    for (i <- 1 to 35) {
      gc.strokeLine(0, canvas.getHeight - 50.5 - i * 50, canvas.getWidth, canvas.getHeight - 50.5 - i * 50)
    }
    gc.translate(-50.5, 0)

    for (i <- 1 to 35) {
      gc.fillText((i / 10.0).toString + " s", 10, canvas.getHeight - 50.5 - i * 50)
    }
  }

  def drawShapes(index: Int, process: MySqlProcess, color: Color, record: Int) {
    val gc = canvas.getGraphicsContext2D
    gc.setFill(Color.BLACK)

    gc.translate(60, 10)
    gc.clearRect(0,0,350,20)
    gc.fillText(record.toString, 15, 10)
    gc.translate(-60, - 10)

    gc.setLineWidth(0.5)
    gc.setStroke(color)
    gc.setFill(color)
    gc.translate(50.5, -50.5)
    //gc.getPixelWriter.setColor(ds.prev._1.toInt, (canvas.getHeight - 50 - ds.prev._2 * 1000).toInt, color)
    val m = process.divider
    gc.strokeLine(process.prev._1 / m, canvas.getHeight - process.prev._2 * 500, process.next._1 / m, canvas.getHeight - process.next._2 * 500)
    gc.translate(-50.5, 50.5)
    gc.translate(60, index * 20 + 30)
    gc.clearRect(0,0,350,20)
    gc.fillText(f"${process.prev._2}%1.4f".toString, 15, 10)
    gc.fillText(process.desc, 70, 10)
    gc.fillRect(1,2,6,7)
    gc.translate(-60, - index * 20 - 30)
  }
}
