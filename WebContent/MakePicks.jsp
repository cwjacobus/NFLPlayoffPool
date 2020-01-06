<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<title>NFL Playoffs Pool - Make Picks</title>
	<style type="text/css">
		.win {
			color: green;
			font-weight: bold;
		}
		.lose {
			color: red;
			font-weight: bold;
		}
	</style>
	
	<script type="text/javascript">

    /*
    2018
    var nfcTeams = {1: "NO", 2: "LAR", 3: "CHI", 4: "DAL", 5: "SEA", 6: "PHI"};
	var afcTeams = {1: "KC", 2: "NE", 3: "HOU", 4: "BAL", 5: "LAC", 6: "IND"}; */
	var nfcTeams = {1: "SF", 2: "GB", 3: "NO", 4: "PHI", 5: "SEA", 6: "MIN"};
	var afcTeams = {1: "BAL", 2: "KC", 3: "NE", 4: "HOU", 5: "BUF", 6: "TEN"};
	/*for (var key in nfcTeams) {
		alert("key " + key + " has value " + nfcTeams[key]);
	}*/
	
	function addDropDown(team, seed, gameCode, ddElement) {
		var option = document.createElement('option');
		option.text = team;
		option.value = seed + ':' + team + ':' + gameCode;
		ddElement.add(option);
	}

    function initializeWC() {
    	/* Assumes playoff games are imported as AFC 4v5, 3v6 and NFC 4v5, 3v6*/
    	addDropDown('', 0, '', afcwc2);
		addDropDown(afcTeams[4], 4, 'afcwc2', afcwc2);
		addDropDown(afcTeams[5], 5, 'afcwc2', afcwc2);
		
		addDropDown('', 0, '', afcwc1);
		addDropDown(afcTeams[3], 3, 'afcwc1', afcwc1);
		addDropDown(afcTeams[6], 6, 'afcwc1', afcwc1);
		
		addDropDown('', 0, '', nfcwc2);
		addDropDown(nfcTeams[4], 4, 'nfcwc2', nfcwc2);
		addDropDown(nfcTeams[5], 5, 'nfcwc2', nfcwc2);

		addDropDown('', 0, '', nfcwc1);
		addDropDown(nfcTeams[3], 3, 'nfcwc1', nfcwc1);
		addDropDown(nfcTeams[6], 6, 'nfcwc1', nfcwc1);
    }

	function getDivValues(conference) {
		seed1 = document.createElement('option');
		seed2 = document.createElement('option');
		var sb = document.getElementById("sb");
		if (conference == 'nfc') {
			wc1 = document.getElementById("nfcwc1");
			wc2 = document.getElementById("nfcwc2");
			div1 = document.getElementById("nfcdiv1");
			div2 = document.getElementById("nfcdiv2");
			champ = document.getElementById("nfcchamp");
			seed1.text = nfcTeams[1];
			seed1.value = 1 + ':' + nfcTeams[1] + ':nfcdiv1';
			seed2.text = nfcTeams[2];
			seed2.value = 2 + ':' + nfcTeams[2] + ':nfcdiv2';
		}
		else {
			wc1 = document.getElementById("afcwc1");
			wc2 = document.getElementById("afcwc2");
			div1 = document.getElementById("afcdiv1");
			div2 = document.getElementById("afcdiv2");
			champ = document.getElementById("afcchamp");
			seed1.text = afcTeams[1];
			seed1.value = 1 + ':' + afcTeams[1] + ':afcdiv1';
			seed2.text = afcTeams[2];
			seed2.value = 2 + ':' + afcTeams[2] + ':afcdiv2';
		} 
		if (wc2.options[wc2.selectedIndex].value != 0 && wc1.options[wc1.selectedIndex].value != 0) {
			removeAllFromDropDown(div1);
			removeAllFromDropDown(div2);
			removeAllFromDropDown(champ);
			removeAllFromDropDown(sb);
			var emptyOption1 = document.createElement('option');
			emptyOption1.text = "";
			emptyOption1.value = 0;
			div1.add(emptyOption1);
            div1.add(seed1);
			var option2 = document.createElement('option');
			var option4 = document.createElement('option');
			var seeding1 = wc1.options[wc1.selectedIndex].value.split(":")[0];
			var seeding2 = wc2.options[wc2.selectedIndex].value.split(":")[0];
			if (seeding1 == 3) {
				option2.text = wc2.options[wc2.selectedIndex].text;
				option2.value = seeding2 + ":" + option2.text + ":" + (conference == 'nfc' ? 'nfcdiv1' : 'afcdiv1');
				option4.text = wc1.options[wc1.selectedIndex].text;
				option4.value = seeding1 + ":" + option4.text  + ":" + (conference == 'nfc' ? 'nfcdiv2' : 'afcdiv2');
			}
			else { // seed = 6
				option2.text = wc1.options[wc1.selectedIndex].text;
				option2.value = seeding1 + ":" + option2.text + ":" + (conference == 'nfc' ? 'nfcdiv1' : 'afcdiv1');
				option4.text = wc2.options[wc2.selectedIndex].text;
				option4.value = seeding2 + ":" + option4.text + ":" + (conference == 'nfc' ? 'nfcdiv2' : 'afcdiv2');
			}
            div1.add(option2);

			var emptyOption2 = document.createElement('option');
			emptyOption2.text = "";
			emptyOption2.value = 0;
			div2.add(emptyOption2);
			div2.add(seed2);
			div2.add(option4);
		}
	}
	
	function getChampValues(conference) {
		if (conference == 'nfc') {
			div1 = document.getElementById("nfcdiv1");
			div2 = document.getElementById("nfcdiv2");
			champ = document.getElementById("nfcchamp");
		}
		else {
			div1 = document.getElementById("afcdiv1");
			div2 = document.getElementById("afcdiv2");
			champ = document.getElementById("afcchamp");
		}
		removeAllFromDropDown(champ);
		removeAllFromDropDown(sb);
		if (div1.options[div1.selectedIndex].value != 0 && div2.options[div2.selectedIndex].value != 0) {
			addDropDown('', 0, '', champ);
			var seeding = div1.options[div1.selectedIndex].value.split(":")[0];
			var option = document.createElement('option');
			option.text = div1.options[div1.selectedIndex].text;
			option.value = seeding + ":" + option.text + ":" + (conference == 'nfc' ? 'nfcchamp' : 'afcchamp');
			champ.add(option);
			option = document.createElement('option');
			seeding = div2.options[div2.selectedIndex].value.split(":")[0];
			option.text = div2.options[div2.selectedIndex].text;
			option.value = seeding + ":" + option.text + ":" + (conference == 'nfc' ? 'nfcchamp' : 'afcchamp');
			champ.add(option);
		}
	}

	function getSBValues() {
		var champ1 = document.getElementById("nfcchamp");
		var champ2 = document.getElementById("afcchamp");
		var sb = document.getElementById("sb");
		removeAllFromDropDown(sb);
		if (champ1.selectedIndex >= 0 && champ2.selectedIndex >= 0 && champ1.options[champ1.selectedIndex].value != 0 && champ2.options[champ2.selectedIndex].value != 0) {
			addDropDown('', 0, '', sb);
			var seeding = champ1.options[champ1.selectedIndex].value.split(":")[0];
			var option = document.createElement('option');
			option.text = champ1.options[champ1.selectedIndex].text;
			option.value = seeding + ":" + champ1.options[champ1.selectedIndex].text + ":sb";
			sb.add(option);
			seeding = champ2.options[champ2.selectedIndex].value.split(":")[0];
			var option = document.createElement('option');
			option.text = champ2.options[champ2.selectedIndex].text;
			option.value = seeding + ":" + champ2.options[champ2.selectedIndex].text + ":sb";
			sb.add(option);
		}
	}
	
	function removeAllFromDropDown(ddElement) {
		var len = ddElement.length;
		for (i=0; i < len;  i++) {
			ddElement.remove(0);
		}
	}
	

 	</script>
 
</head>
<body onload="initializeWC()">
	<form action="savePicks">
	<table cellspacing=10 cellpadding=10>
		<tr><th>WC Round</th><th>Div Round</th><th>Champ</th><th>Super Bowl</th></tr>
		<tr>
		<td width=50> <select name="afcwc1" id="afcwc1" onchange="getDivValues('afc')">
		</select>
      	<td width=50><select name="afcdiv1" id="afcdiv1" onchange="getChampValues('afc')">
		</select></td>
		<td width=50><select name="afcchamp" id="afcchamp" onchange="getSBValues()">
		</select></td>
      	<td></td>
		</tr>

		<tr>
		<td width=50> <select name="afcwc2" id="afcwc2" onchange="getDivValues('afc')">
		</select>
      	<td width=50><select name="afcdiv2" id="afcdiv2" onchange="getChampValues('afc')">
		</select></td>
		<td></td>
      	<td width=35><select name="sb" id="sb">
		</select></td>
		</tr>

		<tr>
		<td width=50> <select name="nfcwc1" id="nfcwc1" onchange="getDivValues('nfc')">
		</select></td>
      	<td width=50><select name="nfcdiv1" id="nfcdiv1" onchange="getChampValues('nfc')">
		</select></td>
		<td width=50><select name="nfcchamp" id="nfcchamp" onchange="getSBValues()">
		</select></td>
      	<td></td>
		</tr>

		<tr>
		<td width=50> <select name="nfcwc2" id="nfcwc2" onchange="getDivValues('nfc')">
		</select>
      	<td width=50><select name="nfcdiv2" id="nfcdiv2" onchange="getChampValues('nfc')">
		</select></td>
		<td></td>
      	<td></td>
		</tr>

	</table>
  	<c:if test="${!sessionScope.readOnly}">
  		<input type="submit" value="Make Picks"/>
  	</c:if>
  	<br><br>
  	<c:choose>
  	<c:when test="${fn:length(picksMap[sessionScope.user.userId]) > 0}">
  	My Picks:<br>
  		<c:forEach var="pick" items="${picksMap[sessionScope.user.userId]}">
  			<c:set var="winLoseClass" value="class='win'"/>
  			<c:if test="${!nflPlayoffsGameMap[pick.gameId].completed}">
  				<c:set var="winLoseClass" value=""/>
  			</c:if>
  			<c:if test="${(nflPlayoffsGameMap[pick.gameId].winner != pick.winner && nflPlayoffsGameMap[pick.gameId].completed)
  							|| (!nflPlayoffsGameMap[pick.gameId].completed && fn:contains(eliminatedTeams, pick.winner))}">
  				<c:set var="winLoseClass" value="class='lose'"/>
  			</c:if>
  			<span ${winLoseClass}>${pick.winner}</span>
  		</c:forEach>
  	</c:when>
  	<c:otherwise>
  		No picks made
  	</c:otherwise>
  	</c:choose>
  	<br><br>
  	<c:if test="${sessionScope.readOnly || sessionScope.user.admin}">
  		All Picks<br>
  		<table>
  		<tr><th>User</th><th colspan=4>Wild card</th><th colspan=4>Division</th><th colspan=2>Champ</th><th align=left>Super Bowl</th></tr>
  		<c:forEach var="picks" items="${picksMap}">
  			<tr>
  			<td>${usersMap[picks.key].userName}</td>
  			<c:forEach var="pick" items="${picks.value}">
  				<c:set var="winLoseClass" value="class='win'"/>
  				<c:if test="${!nflPlayoffsGameMap[pick.gameId].completed}">
  					<c:set var="winLoseClass" value=""/>
  				</c:if>
  				<c:if test="${(nflPlayoffsGameMap[pick.gameId].winner != pick.winner && nflPlayoffsGameMap[pick.gameId].completed)
  								|| (!nflPlayoffsGameMap[pick.gameId].completed && fn:contains(eliminatedTeams, pick.winner))}">
  					<c:set var="winLoseClass" value="class='lose'"/>
  				</c:if>
  				<td align=center ${winLoseClass}>${pick.winner}</td>
  			</c:forEach>
  			</tr>
  		</c:forEach>
  		</table>
  	</c:if>
  	</form>
	</body>
</html>