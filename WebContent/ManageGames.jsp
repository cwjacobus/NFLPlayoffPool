<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>NFL Playoffs Pool</title>
</head>
<body>
	Hello, <s:property value="name"/><br><br>
	Update 20${year} NFL Playoff Games<br><br>
	<s:iterator value="nflPlayoffsGameList" var="nflPlayoffsGame">
  		<form action="updateScore">
  		<table><tr>
      		<td width=100 style="color: white; background: #5D7B9D;"><s:property value="#nflPlayoffsGame.description"/></td>
      		<td>Winner</td>
      		<td><input type="text" name="winner" value="<s:property value="#nflPlayoffsGame.winner"/>" size=3/></td>
      		<td>Loser</td>
      		<td><input type="text" name="loser" value="<s:property value="#nflPlayoffsGame.loser"/>" size=3/></td>
      		<td><input type="submit" value="Set Results"/></td>
      	</tr></table>
      	<input type="hidden" name="gameIndex" value="<s:property value="#nflPlayoffsGame.gameIndex"/>"/>
      	<input type="hidden" name="year" value="${year}"/>
  		</form>
  	</s:iterator>
	</body>
</html>