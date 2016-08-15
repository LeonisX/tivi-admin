package md.leonis.tivi.admin

import java.sql.{Connection, DriverManager, ResultSet}

import md.leonis.tivi.admin.utils.Config

object JdbcUtils {
  Config.loadProtectedProperties()

  private val driver = "com.mysql.jdbc.Driver"
  private val url = "jdbc:mysql://localhost/tests"
  private val username = "root"
  private val password = Config.testDbPassword

  private var connection: Option[Connection] = None

  // throw ClassNotFoundException
  Class.forName(driver)

  def getConnection: Connection = connection match {
    case Some(conn) => conn
    case None =>
      val conn = DriverManager.getConnection(url, username, password)
      connection = Some(conn)
      conn
  }

  def executeUpdate(query: String): Unit = {
    try {
      JdbcUtils.getConnection.createStatement.executeUpdate(query)
    } catch {
      // MySQLSyntaxErrorException, SQLException
      case e: Exception => e.getMessage
    }
  }

  def executeQuery(query: String): ResultSet = {
    JdbcUtils.getConnection.createStatement.executeQuery(query)
  }

}
