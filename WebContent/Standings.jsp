<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>

.odd {
	color: white; 
	background: #5D7B9D; 
	font-weight: bold;
}

.even {
	color: white; 
	background: tan; 
	font-weight: bold;
}

</style>

<html>
<head>
<title>NFL Playoffs Pool</title>
</head>
<body>
    Hello, <s:property value="name"/><br><br>
	<h2>Standings</h2>
	<table style="padding: 0; border-spacing: 0;">
	<tr class="odd"><th></th><th>Name</th>
	<c:choose>
		<c:when test="${maxPoints == true}">
			<th><a href="getStandings?maxPoints=true&year=${year}&name=${name}" class="odd">Max</a></th><th><a href="getStandings?maxPoints=false&year=${year}&name=${name}" class="odd">Pts</a></th>
		</c:when>
		<c:otherwise>
			<th><a href="getStandings?maxPoints=false&year=${year}&name=${name}" class="odd">Pts</a></th><th><a href="getStandings?maxPoints=true&year=${year}&name=${name}" class="odd">Max</a></th>
		</c:otherwise>
	</c:choose>
	</tr>
	<c:set var="colorClass" value="" />
	<c:set var="index" value="0" />
  	<s:iterator value="standings" var="standingsLine">
  		<c:choose>
  		<c:when test = "${index % 2 == 0}">
  			<c:set var="colorClass" value="even" />
  		</c:when>
  		<c:otherwise>
  			<c:set var="colorClass" value="odd" />
  		</c:otherwise>
  		</c:choose>
    	<tr class="${colorClass}">
    		<td><s:property value="#standingsLine.value.place"/></td>
    		<td><s:property value="#standingsLine.value.userName"/></td>
    		<c:choose>
				<c:when test="${maxPoints == true}">
					<td><s:property value="#standingsLine.value.maxPoints"/></td>
    				<td><s:property value="#standingsLine.value.points"/></td>
				</c:when>
				<c:otherwise>
					<td><s:property value="#standingsLine.value.points"/></td>
    				<td><s:property value="#standingsLine.value.maxPoints"/></td>
				</c:otherwise>
			</c:choose>
    	</tr>
    	<c:set var="index" value="${index + 1}"/>
	</s:iterator> 
	</table>
  	<br>
  	<a href="/NFLPlayoffPool/manageGames?year=${year}&name=${name}">Manage NFL Playoff Games</a>
	</body>
</html>