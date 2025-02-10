<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<title>Select NFL Playoff Teams and Create Games</title>
	</head>
	<body>
		<h2>Select NFL Playoff Teams and Create Games</h2>
		
		<br>
		- Use NFLTeam.ShortName<br><br>
		- 13 NFLPlayoffsGame will be created (14 team playoff)<br><br>
		Round 1: AFC 1. 7v2 2. 6v3 3. 5v4 NFC 4. 7v2 5. 6v3 6. 5v4<br>
		Round 2: AFC 1. TBDv1 2. TBDvTBD NFC 3. TBDv1 4. TBDvTBD<br>
		Round 3: AFC 1. R2G1vR2G2 NFC 2. R2G3vR2G4<br>
		Round 4: 1. R3G1vR3G2 (Super Bowl)<br><br>
		<form action="createNFLPlayoffGames">
      		<table>
      			<tr><th>AFC Seed</th><th>Team</th></tr>
      			<c:forEach var = "i" begin = "1" end = "7">
         			<tr><td><c:out value = "${i}"/>.</td><td><input type="text" name="afcSeed" size=6/></td><td>
      			</c:forEach>
      		</table>
      		<table>
      			<tr><th>NFC Seed</th><th>Team</th></tr>
      			<c:forEach var = "i" begin = "1" end = "7">
         			<tr><td><c:out value = "${i}"/>.</td><td><input type="text" name="nfcSeed" size=6/></td><td>
      			</c:forEach>
      		</table>
      		<input type="hidden" name="createFirstGameDateTime" value="${createFirstGameDateTime}"/>
      		<input type="submit" value="Create NFL Playoff Games"/>
      	</form>
	</body>
</html>