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
   	<form action="getStandings"  onsubmit="loginButton.disabled = true; return true;">
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
			 <option value="7">Sculley 2019</option>
			 <option value="8">Jacobus 2019</option>
			 <option value="10">Sculley 2020</option>
			 <option value="9">Jacobus 2020</option>
			 <option value="11">Jacobus 2021</option>
			 <option value="12">Sculley 2021</option>
			 <option value="13">Jacobus 2022</option>
			 <option value="14">Sculley 2022</option>
			 <option value="15">Jacobus 2023</option>
			 <option value="16">Sculley 2023</option>
			 <option value="17">Jacobus 2024</option>
			 <option value="18">Sculley 2024</option>
			 <option value="19">Jacobus 2025</option>
		</select><br>
      	<input type="submit" name= "loginButton" value="Login"/>
  	 </form>
	</body>
</html>