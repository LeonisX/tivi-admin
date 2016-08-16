package md.leonis.tivi.admin

import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.paint.Color

import md.leonis.tivi.admin.JdbcUtils._
import scala.util.control.Breaks._
import scala.collection.mutable.ListBuffer

class DS {
  var prev: (Long, Double) = (-1, 0.0)
  var next: (Long, Double) = (-1, 0.0)

  def add(index: Long, value: Double): DS = {
    prev = next
    next = index -> value
    this
  }
}

object Query {
  val r = scala.util.Random
  val smallSize = 1000
  val smallList = ListBuffer[Double]()
  val bigList = ListBuffer[Double]()

  val in = new DS
  val i2 = new DS
  val i3 = new DS
  val i4 = new DS

  def go(start: Start) {

    executeUpdate("TRUNCATE video;")
    executeUpdate("TRUNCATE video_tag;")



    breakable {
      for (i <- 0 until 10000000) {
        if (!start.running) break
        smallList += insertToVideo
        if (smallList.size >= smallSize) {
          val avg = average(smallList)
          bigList += avg
          smallList.clear()
          /*println(f"${i + 1}%10d: $avg%1.5f")
        println(f"in${i + 1}%8d: $selectFromVideo%1.5f")
        println(f"i2${i + 1}%8d: $selectFromVideo2%1.5f")
        println(f"i3${i + 1}%8d: $selectFromVideo3%1.5f")*/
          in.add(i / smallSize, avg)
          i2.add(i / smallSize, selectFromVideo)
          i3.add(i / smallSize, selectFromVideo2)
          i4.add(i / smallSize, selectFromVideo3)
          /*start.drawShapes(in, Color.GRAY)
        start.drawShapes(i2, Color.GRAY)
        start.drawShapes(i3, Color.GRAY)
        start.drawShapes(i4, Color.GRAY)*/
          start.drawShapes(in, Color.BLACK)
          start.drawShapes(i2, Color.BLUE)
          start.drawShapes(i3, Color.GREEN)
          start.drawShapes(i4, Color.RED)
        }
      }
    }
/*
    def drawShapes(ds: DS, color: Color) {
      val gc = canvas.getGraphicsContext2D
      gc.setStroke(color)
      gc.setLineWidth(0.5)
      gc.strokeLine(ds.prev._1, 800 - ds.prev._2 * 1000, ds.next._1, 800 - ds.next._2 * 1000)
    }*/

    def insertToVideo(): Double = {
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

    def selectFromVideo: Double = {
      val start = System.nanoTime()
      val rs = executeQuery("SELECT * FROM video WHERE tags = '" + "tag" + r.nextInt(100) + "'")
      (System.nanoTime() - start) / 1000000000.0
    }

    def selectFromVideo2: Double = {
      val start = System.nanoTime()
      val rs = executeQuery("SELECT * FROM video WHERE tags LIKE '" + "tag" + r.nextInt(100) + "%'")
      (System.nanoTime() - start) / 1000000000.0
    }

    def selectFromVideo3: Double = {
      val start = System.nanoTime()
      val rs = executeQuery("SELECT * FROM video WHERE tags LIKE '%" + "tag" + r.nextInt(100) + "%'")
      (System.nanoTime() - start) / 1000000000.0
    }

    def randomString(len: Int): String = {
      (1 to len).map(i => r.nextPrintableChar).mkString
    }

    //bigList.foreach(time => println(f"ns: $time%1.5f"))

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

  def average[T]( ts: Iterable[T] )( implicit num: Numeric[T] ) = {
    num.toDouble( ts.sum ) / ts.size
  }
}
