<%@ page contentType="text/html;charset=utf-8" %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<jsp:useBean id="chk" class="tags.ClientValidator">
<jsp:setProperty name="chk" property="*" />
</jsp:useBean>

<%-- получаем корректные значения от ClientValidator --%>
<c:set var="isValid" value="${chk.valid}" />
<c:if test="${isValid}">
	<c:set var="email" value="${chk.email}" scope="request" />
	<c:set var="password" value="${chk.password}" scope="request" />
</c:if>
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Client Checker</title>
</head>
<body>
	<h2>Welcome, <%= request.getParameter("name") %></h2>
	<strong>Email</strong>: <c:out value="${email}" /><br />
	<strong>Password</strong>: <c:out value="${password}" /><br />
</body>

</html>