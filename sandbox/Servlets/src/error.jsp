<%@ page errorPage="/errorHandler.jsp"%>
<html>
<head>
<title>Exception thrower</title>
</head>
<body>
<h2> Throw an IOException</h2>

<% java.io.File file = 
	new java.io.File("z:" + System.getProperty("file.separator") + "temp");
	file.createNewFile();
%>

</body>
</html>