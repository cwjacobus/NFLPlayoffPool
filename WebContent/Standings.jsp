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
	<h2>20${sessionScope.pool.year} Standings</h2>
	<table style="padding: 0; border-spacing: 0;">
	<tr class="odd"><th></th><th>Name</th>
	<c:choose>
		<c:when test="${maxPoints == true}">
			<th><a href="getStandings?maxPoints=true&year=${sessionScope.pool.year}&userName=${userName}&poolName=${poolName}" class="odd">Max</a></th>
			<th><a href="getStandings?maxPoints=false&year=${sessionScope.pool.year}&userName=${userName}&poolName=${poolName}" class="odd">Pts</a></th>
		</c:when>
		<c:otherwise>
			<th><a href="getStandings?maxPoints=false&year=${sessionScope.pool.year}&userName=${userName}&poolName=${poolName}" class="odd">Pts</a></th>
			<th><a href="getStandings?maxPoints=true&year=${sessionScope.pool.year}&userName=${userName}&poolName=${poolName}" class="odd">Max</a></th>
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
  	<br>
  	<a href="/NFLPlayoffPool/makePicks">
  	<c:choose>
  	<c:when test = "${!sessionScope.readOnly}">
  		Make Picks
  	</c:when>
  	<c:otherwise>
  		View Picks
  	</c:otherwise>
  	</c:choose>
  	</a>
  	<br><br>
  	<c:if test = "${allowAdmin}">
  		<a href="/NFLPlayoffPool/manageGames">Enter NFL Playoff Game Scores</a><br><br>
  		<form action="createNFLPlayoffTeams">
  			First Game:&nbsp;<input type="datetime-local" name="createFirstGameDateTime"><br>
  			<input type="submit" value="Create NFL Playoff Teams and Games"><br>
  		</form>
  		<!-- <a href="/NFLPlayoffPool/CreateNFLPlayoffTeams.jsp">Create NFL Playoff Games</a> -->
  		<br>
  		<a href="/NFLPlayoffPool/getPermutations">Get Permutations</a>
  		<br>
  		<h3>Import Data</h3>
  		<form action="import">
  			<input type="file" name="inputFileName" accept=".xls" /><br>
  			<input type="checkbox" name="usersCB" value="Users"> Users<br>
  			<input type="checkbox" name="usersFPCB" value="UsersFY"> Users&nbsp;&nbsp;From Pool ID:&nbsp;<input type="number" name="fromPoolId" min="1" max="99" style="width: 4em"/><br>
			<input type="checkbox" name="gamesCB" value="Games"> Games &nbsp;&nbsp;First Game:&nbsp;<input type="datetime-local" name="firstGameDateTime"><br>
			<input type="checkbox" name="picksCB" value="Picks"> Picks<br>
			<input type="checkbox" name="teamsCB" value="Teams"> Teams (from WS)<br>
			<input type="submit" value="Import">
  		</form>
  		<h3>Add User</h3>
  		<form action="addUser">
  			User Name: <input type="text" name="userName" style="width: 6em"/><br>
  			First Name: <input type="text" name="firstName" style="width: 6em"/><br>
  			Last Name: <input type="text" name="lastName" style="width: 6em"/><br>
  			<input type="submit" value="Add User">
  		</form>
  	</c:if>
	</body>
</html>