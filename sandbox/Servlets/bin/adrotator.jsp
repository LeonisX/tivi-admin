<%@ page contentType="text/html;charset=UTF-8" %>

<% response.setContentType("text/html, UTF-8"); %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<jsp:useBean id="rotator" scope="session" class="tags.Rotator" />
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Adrotator example</title>
	
	<% rotator.nextAd(); %>
</head>
<body>
	<h2>AdRotator Example</h2>
	<p>
		
		<a href="<jsp:getProperty name="rotator" property="link" />">
				<!-- функция ниже аналогично получает ссылку -->
				<%= rotator.getLink() %>
		</a>
	</p>
	
	<h2>Изменение свойства компонента JavaBean</h2>
	
	<jsp:useBean id="user" class="tags.GuestBean" >
	<%-- значения свойств устанавливаются в строгом соответствии с именами параметров --%>
	<jsp:setProperty name="user" property="*" />
	</jsp:useBean>
	
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	<%-- для вывода используем JSTP-элемент c:out --%>
	<%-- ${user.email} эквивалентно getEmail() (синтаксис EL) --%>
	<c:out value="${user.firstName}" /><br />
	<c:out value="${user.lastName}" /><br />
	<c:out value="${user.email}" /><br />
</body>

</html>