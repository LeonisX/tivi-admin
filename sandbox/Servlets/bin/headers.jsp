<%@ page contentType="text/html;charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Зоголовки запроса</title>
</head>
<body>

	<h2>Имена и значения заголовков запроса:</h2>

	<c:forEach var="req" items="${header}">
		<strong><c:out value="${req.key}" /></strong>: 
		<c:out value="${req.value}" /><br />
	</c:forEach>
</body>

</html>