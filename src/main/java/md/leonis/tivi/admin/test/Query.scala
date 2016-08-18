package md.leonis.tivi.admin.test

import javafx.application.Platform
import javafx.scene.paint.Color

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._
import md.leonis.tivi.admin.test.JdbcUtils._

class MySqlProcess {
  var running: Boolean = true
  var threshold: Int = 100
  var count: Int = 1
  var cache: ListBuffer[(Long, Double)] = ListBuffer[(Long, Double)]()
  withCount(count)
  var prev: (Long, Double) = (-1, 0.0)
  var next: (Long, Double) = (-1, 0.0)
  var color: Color = Color.BLACK
  var desc: String = ""

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

  def withColor(color: Color): MySqlProcess = {
    this.color = color
    this
  }

  def withDesc(desc: String): MySqlProcess = {
    this.desc = desc
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

case class QuerySystem(var queries: List[MySqlProcess]) {

  var start: Option[Start] = None

  def process(i: Long) {
    queries.foreach(_.process(i))
  }

  def draw() {
    if (start.isEmpty) return
    Platform runLater new Runnable() {
      def run() {
        for (i <- queries.indices) {
          start.get.drawShapes(i, queries(i), queries(i).color)
        }
      }
    }
  }

  def drawChart() {
    if (start.isEmpty) return
    Platform.runLater(new Runnable() {
      def run() {
        start.get.drawChart()
      }
    })
  }

  def withApplication(start: Start): QuerySystem = {
    this.start = Some(start)
    this
  }

}

object Query {
  val r = scala.util.Random

  //val smallSize = 1000
  //val smallList = ListBuffer[Double]()
  //val bigList = ListBuffer[Double]()



  def go(start: Start) {

    val system = QuerySystem(List(
      MySqlProcess().withFunction(insertToVideo).withColor(Color.BLACK).withThreshold(1).withDesc("INSERT INTO video"),
      MySqlProcess().withFunction(selectFromVideo).withColor(Color.BLUE).withThreshold(1000).withDesc("SELECT video tags=tag"),
      MySqlProcess().withFunction(selectFromVideo2).withColor(Color.VIOLET).withThreshold(1000).withDesc("SELECT video tags LIKE %%"),
      MySqlProcess().withFunction(insertToVideo2).withColor(Color.RED).withThreshold(1).withDesc("INSERT INTO video_index"),
      MySqlProcess().withFunction(selectFromVideoI).withColor(Color.DARKORANGE).withThreshold(1000).withDesc("SELECT video_index tags=tag"),
      MySqlProcess().withFunction(selectFromVideoI2).withColor(Color.GREEN).withThreshold(1000).withDesc("SELECT video_index tags LIKE %%")
    )).withApplication(start)

    executeUpdate("TRUNCATE video;")
    executeUpdate("TRUNCATE video_index;")
    executeUpdate("TRUNCATE video_tag;")

    system.drawChart()

    breakable {
      for (i <- 0 until 10000000) {
        if (!start.running) break
        system.process(i)
        system.draw()
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
    preparedStatement.setString(2, "tag" + r.nextInt(100) + ";")
    //(0 until r.nextInt(10)).map("tag" + r.nextInt(100)).mkString(",")
    preparedStatement.executeUpdate()
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideo() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video WHERE tags = '" + "tag" + r.nextInt(100) + ";" + "'")
    rs.last
    //println("1:" + rs.getRow)
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideo2() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video WHERE tags LIKE '%" + "tag" + r.nextInt(100) + ";" + "%'")
    rs.last
    //println("4:" + rs.getRow)
    (System.nanoTime() - start) / 1000000000.0
  }

  def insertToVideo2() = {
    val start = System.nanoTime()
    val preparedStatement = getConnection.prepareStatement(
      "INSERT INTO video_index (title, locurl, exturl, descript, keywords, textshort, textmore, textnotice, mirrorsname, mirrorsurl, tags) " +
        "VALUES (?, '', '', '', '', '', '', '', '', '', ?)")
    preparedStatement.setString(1, "title " + randomString(7))
    preparedStatement.setString(2, "tag" + r.nextInt(100) + ";")
    //(0 until r.nextInt(10)).map("tag" + r.nextInt(100)).mkString(",")
    preparedStatement.executeUpdate()
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideoI() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video_index WHERE tags = '" + "tag" + r.nextInt(100) + ";" + "'")
    rs.last
    //println("1:" + rs.getRow)
    (System.nanoTime() - start) / 1000000000.0
  }

  def selectFromVideoI2() = {
    val start = System.nanoTime()
    val rs = executeQuery("SELECT * FROM video_index WHERE tags LIKE '%" + "tag" + r.nextInt(100) + ";" + "%'")
    rs.last
    //println("4:" + rs.getRow)
    (System.nanoTime() - start) / 1000000000.0
  }

  def average[T]( ts: Iterable[T] )( implicit num: Numeric[T] ) = {
    num.toDouble( ts.sum ) / ts.size
  }
}
