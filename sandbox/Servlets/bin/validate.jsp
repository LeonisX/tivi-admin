<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<html>
<head>

<c:import url="/js/validate.js" charEncoding="UTF-8" />

<title>Страница помощи</title></head><body>
<h2>Пожалуйста, заполните поля</h2>

<form action ="./" onSubmit=" return validate(this)">

<table border="0"><tr><td valign="top">
Имя: </td>  <td valign="top">
<input type="text" name="username" size="20">
</td></tr><tr><td valign="top">
Email: </td>  <td valign="top">
<input type="text" name="email" size="20">
</td></tr><tr><td valign="top">
<input type="submit" value="Submit Info"></td></tr>
</table></form>
</body></html>