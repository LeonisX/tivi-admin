<%@ page contentType="text/html;charset=utf-8" %>
<%@ page isErrorPage="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="cookieBean" class="tags.CookieBean" />
<jsp:setProperty name="cookieBean" property="name" value="bakedcookie" />
<jsp:setProperty name="cookieBean" property="maxAge"
	 value="<%= 365*24*60*60 %>" />
<jsp:setProperty name="cookieBean" property="path"
	 value="<%= request.getContextPath() %>" />
<jsp:setProperty name="cookieBean" property="cookieHeader"
	 value="<%= response %>" />	 
<html>

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Cookie Maker</title>
</head>
<body>
	<h2>Информация о новой куке:</h2>
	
Имя: <jsp:getProperty name="cookieBean" property="name" /><br />
Значение: <jsp:getProperty name="cookieBean" property="value" /><br />
Путь: <jsp:getProperty name="cookieBean" property="path" /><br />

<h2>Чтение неявно создаваемого объекта cookie:</h2>

<c:choose>
  <c:when test="${empty cookie}" >
  <h2>We did not find any cookies in the request</h2>
  </c:when>
  <c:otherwise>
<h2>The name and value of each found cookie</h2>
<c:forEach var="cookieVal" items="${cookie}">
<strong>Cookie name:</strong> <c:out value="${cookieVal.key}" /><br>
<strong>Cookie value:</strong> <c:out value="${cookieVal.value.value}" /><br><br>
</c:forEach>
</c:otherwise>
</c:choose>
  	
  	
<% Cookie[] cookies = request.getCookies();
for (int i = 0; i < cookies.length; i++) {
    Cookie cookie = cookies[i];%>
        <strong>Cookie name:</strong> <%=cookie.getName()%><br>
        <strong>Cookie value:</strong> <%=cookie.getValue()%><br>
<%}%>

<p>Значение конкретного кука: ${cookie.bakedcookie.value}</p>

<h2>Просмотр сессии из JSP:</h2>

Session id: <c:out value="${pageContext.session.id}"/><br />
Creation time: <c:out value="${pageContext.session.creationTime}"/><br />
Last accessed time: <c:out value="${pageContext.session.lastAccessedTime}"/><br /><br />

<jsp:useBean id="timeValues" class="java.util.Date" />
<c:set target="${timeValues}" value=
	"${pageContext.session.creationTime}" property="time" />
The creation time: <fmt:formatDate value="${timeValues}"
	type="both" dateStyle="medium" /><br />
<c:set target="${timeValues}" value=
	"${pageContext.session.lastAccessedTime}" property="time" />
	
The last accessed time: <fmt:formatDate value="${timeValues}"
	type="both" dateStyle="short" /><br />	




</body>
</html>