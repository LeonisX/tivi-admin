<!-- Дата и время, включаемые в другой документ -->
<%@ page contentType="text/html;charset=utf-8" %>

<table>
	<tr>
		<td style = "background-color: black;">
			<p class = "big" style = "color: cyan; font-size: 3em; font-weight: bold;">
			
			<%-- Сценарий для определения местности клиента и формата даты --%>
			<%
				// получение местности клиента
				java.util.Locale locale = request.getLocale();
				
				// получение формата данных DateFormat для местности клиента
				java.text.DateFormat dateFormat = 
					java.text.DateFormat.getDateTimeInstance(
						java.text.DateFormat.LONG,
						java.text.DateFormat.LONG, locale);
						
			%>
			
			<%= dateFormat.format(new java.util.Date())  %>
			
			</p>
		</td>
	</tr>
</table>