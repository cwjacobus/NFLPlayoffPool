<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>NFL Playoffs Pool</title>
</head>
<body>
	<h1>NFL Playoffs Pool</h1>
   	<form action="getStandings" onsubmit="loginButton.disabled = true; return true;">
   		<table>
   		<tr><td>User Name</td><td><input type="text" name="userName" size="10"/></td></tr>
      	<tr><td>League</td><td>
		<select name="poolName">
      	     <option value="Jacobus">Jacobus</option>
      	     <option value="Sculley">Sculley</option>
		</select></td></tr>
		<tr><td>Year</td><td><input type="number" name="year" min=14 max=2075 size="6"/></td></tr>
      	<tr><td><input type="submit" name= "loginButton" value="Login"/></td></tr>
      	</table>
      	<input type="hidden" name="maxPoints" value=false/>
  	 </form>
	</body>
</html>