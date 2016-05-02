<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<html>
<head><title>Context binding JSP</title></head>
<body>
<h2>Here is the bound ContextObject</h2>
<%-- <jsp:useBean id="contextObj" class="com.jspservletcookbook.ContextObject" /> 
<jsp:useBean id="date" class="java.util.Date" /> 
<c:set var="com.jspservletcookbook.ContextObject" value="${contextObj}" scope="application" />--%>
<c:set target="${applicationScope[\"com.jspservletcookbook.ContextObject\"].map}" value="${date}" property="${pageContext.request.remoteAddr}"/>
<c:out value="${\"com.jspservletcookbook.ContextObject\".values}" escapeXml="false" />
</body>
</html>