<%@ page contentType="text/html;charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/taglib.tld" prefix="mytag" %>
<%-- как вариант uri - "http://tv-games.ru/tag" --%>

<html>

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Post Data Viewer</title>
</head>
<body>

	<h2>Пример 0 - вывод всех параметров:</h2>

	<c:forEach var="map_entry" items="${param}">
		<strong><c:out value="${map_entry.key}" /></strong>: 
		<c:out value="${map_entry.value}" /><br />
	</c:forEach>
	
	<h3>Другой способ:</h3>
	<strong>name</strong>: <c:out value="${param.name}" />
	
	
	<h2>Пример 1 - простой нестандартный тег:</h2>
	
	<mytag:simple />	
	
	<h2>Пример 2 - нестандартный тег с атрибутами:</h2>
	
	<%-- скриптлет для получения параметра запроса name --%>
	<% String name = request.getParameter("name"); %>
	
	<mytag:attr firstName="<%= name %>" />
	
	<h2>Пример 3 - обработка тела нестандартного тега (не пашет):</h2>
	
	<table>
		<thead>
			<th>Фамилия</th>
			<th>Имя</th>
			<th>e-mail</th>
		</thead>
		
		<%-- нестандартный тег guestlist --%>
		<my:guestlist>
			<tr>
				<td></%= lastName %/></td>
				<td></%= firtsName %/></td>
				<td></%= <a href="mailto:</%= email %/>">
					</%= email %/></a></td>
			</tr>
		</my:questlist>
	</table>
</body>

</html>