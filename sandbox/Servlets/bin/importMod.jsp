<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html>
<head>

<c:import charEncoding="UTF-8" url="/js/functions.js" />

<title>Формы клиента</title></head><body>

<h2>Введите своё имя и e-mail</h2>

<form action="./" name="entryForm" onSubmit="return CheckEmail(this.email.value)">

<table border="0"><tr><td valign="top">
Имя и фамилия: </td>  <td valign="top"><input type="text" name="name" size="20"></td></tr>
<tr><td valign="top">
Email: </td>  <td valign="top"><input type="text" name="email" size="20"></td>
<tr><td valign="top"><input type="submit" value="Submit"></td>
</tr></table>

</form>
</body></html>