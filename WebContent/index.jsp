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
      	<select name="year">
             <option value="14">2014</option>
             <option value="15">2015</option>
             <option value="16">2016</option>
             <option value="17">2017</option>
             <option value="18">2018</option>
		</select><br>
      	<input type="submit" value="Login"/>
  	 </form>
	</body>
</html>