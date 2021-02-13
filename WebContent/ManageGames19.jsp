<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>NFL Playoffs Pool</title>
</head>
<body>
	Update 20${sessionScope.pool.year} NFL Playoffs Games<br><br>
	<c:forEach var="nflPlayoffsGame" items="${sessionScope.nflPlayoffsGameMap}">
  		<form action="updateScore">
  		<table><tr>
      		<td width=100 style="color: white; background: #5D7B9D;">${nflPlayoffsGame.value.description}</td>
      		<td>Winner</td>
      		<td><input type="text" name="winner" value="${nflPlayoffsGame.value.winner}" size=3/></td>
      		<td>Loser</td>
      		<td><input type="text" name="loser" value="${nflPlayoffsGame.value.loser}" size=3/></td>
      		<td><input type="submit" value="Set Results"/></td>
      	</tr></table>
      	<input type="hidden" name="gameIndex" value="${nflPlayoffsGame.value.gameIndex}"/>
  		</form>
  	</c:forEach>
	</body>
</html>