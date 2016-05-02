<%@ page contentType="text/html;charset=utf-8" %>
<%@ page isErrorPage="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Просим прощение за эту ошибку</title>
</head>
<body>
	<h2>Перехвачена ошибка:</h2>
	
Request URI:
<c:out value="${pageContext.errorData.requestURI}" />
<br><br>
The exception message: 
  <c:out value="${pageContext.exception.message}" />
  	
</body>

</html>