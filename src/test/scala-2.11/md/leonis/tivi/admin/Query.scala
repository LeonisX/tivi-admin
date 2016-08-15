package md.leonis.tivi.admin

import md.leonis.tivi.admin.JdbcUtils._

object Query {
  def main(args: Array[String]) {

    executeUpdate("TRUNCATE video;")
    executeUpdate("TRUNCATE video_tag;")

    val preparedStatement = getConnection.prepareStatement(
      "INSERT INTO video (title, locurl, exturl, descript, keywords, textshort, textmore, textnotice, mirrorsname, mirrorsurl, tags) " +
        "VALUES (?, '', '', '', '', '', '', '', '', '', ?)")
    preparedStatement.setString(1, "title")
    preparedStatement.setString(2, "tags")
    preparedStatement.executeUpdate()
    preparedStatement.setString(1, "title2")
    preparedStatement.setString(2, "tags2")
    preparedStatement.executeUpdate()

    val ps = getConnection.prepareStatement("SELECT * FROM video")
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
    }

    def nonEmpty(str: String): Boolean = {
      str.nonEmpty && str != "0"
    }
  }
}
