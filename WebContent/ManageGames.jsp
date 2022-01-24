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
		<c:set var="visTeam" value=""/>
		<c:set var="visReadOnly" value=""/>
		<c:set var="homeTeam" value=""/>
		<c:set var="homeReadOnly" value=""/>
		<c:if test="${nflPlayoffsGame.value.visitor != null && nflPlayoffsGame.value.visitor != ''}">
			<c:set var="visTeam" value="${sessionScope.nflTeamsMapById.get(nflPlayoffsGame.value.visitor).shortName}"/>
			<c:set var="visReadOnly" value="readonly"/>
		</c:if>
		<c:if test="${nflPlayoffsGame.value.home != null && nflPlayoffsGame.value.home != ''}">
			<c:set var="homeTeam" value="${sessionScope.nflTeamsMapById.get(nflPlayoffsGame.value.home).shortName}"/>
			<c:set var="homeReadOnly" value="readonly"/>
		</c:if>
  		<form action="updateScore">
  		<table>
  			<!-- <tr><th>Game</th><th>Vis Team</th><th>VisScore</th><th>Home Team</th><th>Home Score</th><th></th></tr>-->
  		    <tr>
      		<td width=100 style="color: white; background: #5D7B9D;">${nflPlayoffsGame.value.description}</td>
      		<td>V Team</td>
      		<td><input type="text" name="visitor" value="${visTeam}" style="width: 3em" ${visReadOnly}/></td>
      		<td>V Score</td>
      		<td><input type="number" name="visScore" value="${nflPlayoffsGame.value.visScore}" style="width: 3em" min="0" max="99"/></td>
      		<td>H Team</td>
      		<td><input type="text" name="home" value="${homeTeam}" style="width: 3em" ${homeReadOnly}/></td>
      		<td>H Score</td>
      		<td><input type="number" name="homeScore" value="${nflPlayoffsGame.value.homeScore}" style="width: 3em" min="0" max="99"/></td>
      		<td><input type="submit" value="Set Score"/></td>
      	    </tr>
      	</table>
      	<input type="hidden" name="gameIndex" value="${nflPlayoffsGame.value.gameIndex}"/>
  		</form>
  	</c:forEach>
	</body>
</html>