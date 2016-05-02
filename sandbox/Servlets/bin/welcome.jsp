<%@ page contentType="text/html;charset=utf-8" %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Обработка GET запросов, содержащих данные</title>
</head>
<body>
	<% 		// начало скриптлета
		
		String name = request.getParameter("firstName");
		
		if (name != null) {
		
	%> 		<%-- закрытие скриптлета для вставки данных с неизменной структурой --%>
	
		<h1>
			Здравствуй, <%= name %>, <br />
			Добро пожаловать в мир JSP!
	
	<% 		// продолжение скриптлета
		
		} 	// конец блока if
		else {
		
	%>  	<%-- закрытие скриптлета для вставки данных с неизменной структурой --%>
	
		<form action = "welcome.jsp" method = "get">
			<p>Введите ваше имя и нажмите кнопку "Отправить"</p>
			<p>	<input type = "text" name = "firstName" />
				<input type = "submit" value = "Отправить" />
			</p>
		</form>
	
	<% 		// продолжение скриптлета
		
		} 	// конец блока else
		
	%>  	<%-- конец скриптлета --%>
	
</body>

</html>