import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

// доступ к данным через DataSource (и пул коннекшенов)
// тут не использован коммит-ролбэк, они стоят на автомате.
// их следует использовать если выполняется несколько запросов в 1 транзакции

public class DBServlet extends HttpServlet {
	private static final long serialVersionUID = -2755247387424114309L;
	DataSource pool;
	
	@Override
	public void init() throws ServletException {
		Context env = null;
		try {
			env = (Context) new InitialContext().lookup("java:comp/env");
			// ищем DataSource, который представляет пул соединений
			pool = (DataSource) env.lookup("jdbc/LeoDB");
			if (pool == null) {
				throw new ServletException("'jdbc/LeoDB' is not a unknown DataSource");
			}
		} catch (NamingException e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String sql = "SELECT * FROM leo_test.books";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;
		// начинаем создание HTML-страницы
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head>");
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<title>Typical Database Access</title></head><body>");
		out.println("<h2>Database info</h2>");
		
		out.println("<table border=\"1\">");
		try {
			// получаем соединение из пула соединений
			conn = pool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsm = rs.getMetaData();
			int colCount = rsm.getColumnCount();
			//out.println(colCount);
			//out.println(rsm.getColumnName(0));
			out.println("<tr><th>Различные свойства</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnName(i) + "</th>");
			}
			out.println("</tr>");
			//ради понтов вывожу другие данные столбцов
			out.println("<tr><th>ColumnName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>ColumnLabel</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnLabel(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>ColumnClassName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnClassName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>ColumnTypeName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnTypeName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>ColumnDisplaySize</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getColumnDisplaySize(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>Precision</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getPrecision(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>Scale</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getScale(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>SchemaName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getSchemaName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>CatalogName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getCatalogName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>TableName</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>" +rsm.getTableName(i) + "</th>");
			}
			out.println("</tr>");
			out.println("<tr><th>");
			out.println("Currency<br />");
			out.println("CaseSensitive<br />");
			out.println("AutoIncrement<br />");
			out.println("Nullable<br />");
			out.println("ReadOnly<br />");
			out.println("Searchable<br />");
			out.println("Signed<br />");
			out.println("Writable");
			out.println("</th>");
			for (int i = 1; i <= colCount; i++) {
				out.println("<th>");
				out.println(rsm.isCurrency(i) + "<br />");
				out.println(rsm.isCaseSensitive(i) + "<br />");
				out.println(rsm.isAutoIncrement(i) + "<br />");
				out.println(rsm.isNullable(i) + "<br />");
				out.println(rsm.isReadOnly(i) + "<br />");
				out.println(rsm.isSearchable(i) + "<br />");
				out.println(rsm.isSigned(i) + "<br />");
				out.println(rsm.isWritable(i));
				out.println("</th>");
			}
			out.println("</tr>");
			
			// собственно, данные таблицы
			while (rs.next()) {
				out.println("<tr><td></td>");
				for (int i = 1; i<= colCount; i++) {
					out.println("<td>" + rs.getString(i) + "</td>");
				}
				out.println("</tr>");
			}
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// очень важно. этот код возвращает соединение в пул
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) { }
		}
		out.println("</table></body></html>");
	}


}
