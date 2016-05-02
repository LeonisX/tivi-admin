<!-- кодировка JSP страницы --> <%-- JSP комментарий - в html не показывается --%>
<%@ page contentType="text/html;charset=utf-8" %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<!-- обновляем страницу каждые 10 секунд -->
	<meta http-equiv = "refresh" content = "10" />
	<title>Простой пример JSP</title>

	<style type = "text/css">
		.big {  font-family: hevletica, arial, sans-serif;
				font-weight: bold;
				font-size: 2em; }
	</style>	
</head>
<body>
	<p class="big">Простой пример JSP</p>
	
	<table style = "border: 6px outset;">
		<tr>
			<td style = "background-color: black;">
				<p class = "big" style = "color: cyan;">
				
				<!-- выражение JSP для вставки даты/времени -->
				<%= new java.util.Date() %>
			
				<%-- объявление --%>
				<%! int counter = 0; /* комментарий языка сценариев в коде. В html не показывается */ %>
				</p>
			</td>
		</tr>
	</table>
</body>

</html>