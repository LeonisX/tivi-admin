<%@ page contentType="text/html;charset=utf-8" %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Использование jsp:include</title>
	
	<style type = "text/html">
		body {
			font-family: tahoma, hevletica, arial, sans-serif;
		}
		
		table, tr, td {
			font-size: .9em;
			border: 3px groove;
			padding: 5px;
			background-color: #dddddd;
		}
	</style>			
</head>
<body>
	<table>
		<tr>
			<td style = "width: 160px; text-align: center">
				<img src="avatar3_14.gif" />
			</td>
		<td>
	
			<%-- включение banner.html --%>
			<jsp:include page = "banner.html" flush = "true" />
	
		</td>
	</tr>
	<tr>
		<td>	
			<jsp:include page = "toc.html" flush = "true" />
		</td>
		<td>
			<jsp:include page = "clock2.jsp" flush = "true" />
		</td>
	</tr>
</table>
</body>

</html>