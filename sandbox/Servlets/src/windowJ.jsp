<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<html>
<head>

<c:import url="/WEB-INF/javascript/functions.js" />

<title>Help Page</title></head><body>
<h2>Cookie Info</h2>

<form action ="" onSubmit=" return false">
<table border="0"><tr><td valign="top">
Click on the button to get more info on cookies: </td>  
<td valign="top">

<input type="button" name="button1" value=
"More Info" onClick=
"CreateWindow('<c:out value=
"${pageContext.request.contextPath}${initParam[\"jsp-url\"]}"/>')">

</td></tr>
</table></form>
</body></html>
