<%@ page contentType="text/html;charset=utf-8" %>

<?xml version = "1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Processing a forwarded request</title>
	
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

<p class="big">
	Hello <%= request.getParameter("firstName") %>, <br />
	Your request was received <br /> and forwarded at
</p>

<table>
	<tr>
		<td style = "border: 6px outset;">
			<p class = "big" style = "color: cyan;">
			
			<%= request.getParameter("date") %>
			
			</p>
		</td>
	</tr>
</table>

</body>

</html>