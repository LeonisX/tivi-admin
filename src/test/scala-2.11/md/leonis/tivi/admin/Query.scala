package md.leonis.tivi.admin

import javafx.application.Platform
import javafx.scene.paint.Color

import md.leonis.tivi.admin.JdbcUtils._

import scala.util.control.Breaks._
import scala.collection.mutable.ListBuffer

class MySqlProcess {
  var running: Boolean = true
  var threshold: Int = 100
  var count: Int = 1
  var cache: ListBuffer[(Long, Double)] = ListBuffer[(Long, Double)]()
  withCount(count)
  var prev: (Long, Double) = (-1, 0.0)
  var next: (Long, Double) = (-1, 0.0)

  var function = () => 0.0

  def process(index: Long): MySqlProcess = {
    if (index % threshold == 0) {
      cache.clear()
      (1 to count).foreach(i => cache.append(index -> function()))
      prev = next
      var result = average
      val diff = result - prev._2
      if (Math.abs(diff) > prev._2 / 10) result = prev._2 + diff / 5
      next = index -> result
    }
    this
  }

  def withFunction(function: () => Double): MySqlProcess = {
    this.function = function
    this
  }

  def withThreshold(threshold: Int): MySqlProcess = {
    this.threshold = threshold
    this
  }

  def withCount(count: Int): MySqlProcess = {
    this.count = count
    this
  }

  def stop(): Unit = {
    running = false
  }

  def average = {
    cache.map(c => c._2).sum / count
  }
}

object MySqlProcess {
  val scale: Int = 1000
  def apply() = new MySqlProcess
}

object Query {
  val r = scala.util.Random
  //val smallSize = 1000
  //val smallList = ListBuffer[Double]()
  //val bigList = ListBuffer[Double]()

  val in = MySqlProcess().withFunction(insertToVideo).withThreshold(1)
  val i2 = MySqlProcess().withFunction(selectFromVideo).withThreshold(1000).withCount(1)
  val i3 = MySqlProcess().withFunction(selectFromVideo2).withThreshold(1000).withCount(1)
  val i4 = MySqlProcess().withFunction(selectFromVideo3).withThreshold(1000).withCount(1)

  def go(start: Start) {

    executeUpdate("TRUNCATE video;")
    executeUpdate("TRUNCATE video_tag;")

    breakable {
      for (i <- 0 until 10000000) {
        if (!start.running) break
          in.process(i)
         i2.process(i)
          i3.process(i)
          i4.process(i)

            Platform.runLater(new Runnable() {
              def run() {
                start.drawShapes(in, Color.BLACK)
                start.drawShapes(i2, Color.BLUE)
                start.drawShapes(i3, Color.GREEN)
                start.drawShapes(i4, Color.RED)
              }
            })


        //start.canvas.getGraphicsContext2D.save()
      }
    }

    for (i <- 1 to 100) {
      val preparedStatement2 = getConnection.prepareStatement(
        "INSERT INTO video_tag (tagcpu, tagword, tagrating) " +
          "VALUES (?, ?, 0)")
      preparedStatement2.setString(1, "cpu" + i)
      preparedStatement2.setString(2, "tag" + i)
      preparedStatement2.executeUpdate()
    }

    /*val ps = getConnection.prepareStatement("SELECT * FROM video")
    //выполняем запрос
    val result = ps.executeQuery()
    val rsmd = result.getMetaData
    while (result.next()) {
      for (i <- 1 to rsmd.getColumnCount) {
        val columnValue = result.getString(i)
        if (i > 1) if (nonEmpty(columnValue)) print(",  ")
        if (nonEmpty(columnValue))
          print(rsmd.getColumnName(i) + ": " + columnValue)
      }
      println()
    }*/

    /*def nonEmpty(str: String): Boolean = {
      str.nonEmpty && str != "0"
    }*/
  }

  def randomString(len: Int): String = {
    (1 to len).map(i => r.nextPrintableChar).mkString
  }

  def insertToVideo() = {
    val start = System.nanoTime()
    val preparedStatement = getConnection.prepareStatement(
      "INSERT INTO video (title, locurl, exturl, descript, keywords, textshort, textmore, textnotice, mirrorsname, mirrorsurl, tags) " +
        "VALUES (?, '', '', '', '', '', '', '', '', '', ?)")
    preparedStatement.setString(1, "title " + randomString(7))
    preparedStatement.setString(2, "tag" + r.nextInt(100))
    //(0 until r.nextInt(10)).map("tag" + r.nextInt(100)).mkString(",")
    preparedStatement.executeUpdate()
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideo() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video WHERE tags = '" + "tag" + r.nextInt(100) + "'")
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideo2() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video WHERE tags LIKE '" + "tag" + r.nextInt(100) + "%'")
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideo3() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video WHERE tags LIKE '%" + "tag" + r.nextInt(100) + "%'")
    (System.nanoTime() - start) / 1000000000.0
  }

  def average[T]( ts: Iterable[T] )( implicit num: Numeric[T] ) = {
    num.toDouble( ts.sum ) / ts.size
  }
}
