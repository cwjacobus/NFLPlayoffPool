<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Create NFL Playoffs Pool</title>
</head>
<body>
	<h2>Create NFL Playoffs Pool</h2>
   	<form action="createPool" onsubmit="createPoolButton.disabled = true; return true;">
   		<table>
      	<tr><td>League</td><td>
		<select name="poolName">
      	     <option value="Jacobus">Jacobus</option>
      	     <option value="Sculley">Sculley</option>
		</select></td></tr>
		<tr><td>Year</td><td><input type="number" name="year" min=14 max=2075 style="width: 5em"/></td></tr>
		<tr><td>R1 Pts</td><td><input type="number" name="pointsRd1" value="3" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>R2 Pts</td><td><input type="number" name="pointsRd2" value="5" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>Champ Pts</td><td><input type="number" name="pointsChamp" value="10" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>SB Pts</td><td><input type="number" name="pointsSB" value="20" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td></td><td><input type="checkbox" name="copyUsers" value="true">Copy Users From Previous Year</td></tr>
      	<tr><td><input type="submit" name= "createPoolButton" value="Create"/></td></tr>
      	</table>
  	 </form>
	</body>
</html>