<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
   pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
   <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>NFL Playoffs Pool</title>
</head>
<body>
	<h1>NFL Playoffs Pool</h1>
   	<form action="getStandings">
      	<label for="name">Please enter your user name</label><br/>
      	<input type="text" name="name"/><br>
      	<input type="hidden" name="maxPoints" value=false/>
		<select name="poolId">
      	     <option value="0"></option>
      	     <option value="1">Sculley 2014</option>
             <option value="2">Sculley 2015</option>
			 <option value="3">Sculley 2016</option>
             <option value="4">Sculley 2017</option>
             <option value="5">Sculley 2018</option>
			 <option value="6">Jacobus 2018</option>
		</select><br>
      	<input type="submit" value="Login"/>
  	 </form>
	</body>
</html>