<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- Если без /jsp: The exception message: /coreTags.jsp (line: 9, column: 0) According to TLD or attribute directive in tag file, attribute test does not accept any expressions -->
<html>
<head><title>Использование основных (базовых) JSTL тэгов</title></head>
<body>
<h2>Вот часовые пояса, доступные в этой системе:</h2>
<jsp:useBean id="zone" class="tags.ZoneWrapper" /> 
<jsp:useBean id="date" class="java.util.Date" /> 

<c:if test="${date.time != 0}" >

    <c:out value="Уф, время ещё не остановилось...<br /><br />" escapeXml="false" />

</c:if>

<c:set var="zones" value="${zone.availableIDs}" scope="session" />

    <c:forEach var="id" items="${zones}">

        <c:out value="${id}<br />" escapeXml="false" />


    </c:forEach>

</body>