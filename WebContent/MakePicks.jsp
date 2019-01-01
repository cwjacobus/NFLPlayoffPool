<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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

    var nfcTeams = {1: "NO", 2: "LAR", 3: "CHI", 4: "DAL", 5: "SEA", 6: "PHI"};
	var afcTeams = {1: "KC", 2: "NE", 3: "HOU", 4: "BAL", 5: "LAC", 6: "IND"};
	/*for (var key in nfcTeams) {
		alert("key " + key + " has value " + nfcTeams[key]);
	}*/

    function initializeWC() {
		var emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		afcwc1.add(emptyOption);
		var option = document.createElement('option');
		option.text = afcTeams[3];
		option.value = 3;
		afcwc1.add(option);
		option = document.createElement('option');
		option.text = afcTeams[6];
		option.value = 6;
		afcwc1.add(option);

		emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		afcwc2.add(emptyOption);
		option = document.createElement('option');
		option.text = afcTeams[4];
		option.value = 4;
		afcwc2.add(option);
		option = document.createElement('option');
		option.text = afcTeams[5];
		option.value = 5;
		afcwc2.add(option);

		emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		nfcwc1.add(emptyOption);
		option = document.createElement('option');
		option.text = nfcTeams[3];
		option.value = 3;
		nfcwc1.add(option);
		option = document.createElement('option');
		option.text = nfcTeams[6];
		option.value = 6;
		nfcwc1.add(option);

		emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		nfcwc2.add(emptyOption);
		option = document.createElement('option');
		option.text = nfcTeams[4];
		option.value = 4;
		nfcwc2.add(option);
		option = document.createElement('option');
		option.text = nfcTeams[5];
		option.value = 5;
		nfcwc2.add(option);
    }

	function getDivValues(conference) {
		seed1 = document.createElement('option');
		seed1.value = 1;
		seed2 = document.createElement('option');
		seed2.value = 2;
		if (conference == 'nfc') {
			wc1 = document.getElementById("nfcwc1");
			wc2 = document.getElementById("nfcwc2");
			div1 = document.getElementById("nfcdiv1");
			div2 = document.getElementById("nfcdiv2");
			seed1.text = nfcTeams[1];
			seed2.text = nfcTeams[2];
		}
		else {
			wc1 = document.getElementById("afcwc1");
			wc2 = document.getElementById("afcwc2");
			div1 = document.getElementById("afcdiv1");
			div2 = document.getElementById("afcdiv2");
			seed1.text = afcTeams[1];
			seed2.text = afcTeams[2];
		}
		//alert(wc1.options[wc1.selectedIndex].text + " " + wc2.options[wc2.selectedIndex].text);
		if (wc2.options[wc2.selectedIndex].value != 0 && wc1.options[wc1.selectedIndex].value != 0) {
			var len = div1.length;
			for (i=0; i< len;  i++) {
				div1.remove(0);
			}
			len = div2.length;
			for (i=0; i< len;  i++) {
				div2.remove(0);
			}
			var emptyOption1 = document.createElement('option');
			emptyOption1.text = "";
			emptyOption1.value = 0;
			div1.add(emptyOption1);
            div1.add(seed1);
			var option2 = document.createElement('option');
			var option4 = document.createElement('option');
			if (wc1.options[wc1.selectedIndex].value == 3) {
				option2.text = wc2.options[wc2.selectedIndex].text;
				option2.value = wc2.options[wc2.selectedIndex].value;
				option4.text = wc1.options[wc1.selectedIndex].text;
				option4.value = wc1.options[wc1.selectedIndex].value;
			}
			else {
				option2.text = wc1.options[wc1.selectedIndex].text;
				option2.value = wc1.options[wc1.selectedIndex].value;
				option4.text = wc2.options[wc2.selectedIndex].text;
				option4.value = wc2.options[wc2.selectedIndex].value;
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
		var len = champ.length;
		for (i=0; i< len;  i++) {
			champ.remove(0);
		}
		var len = sb.length;
		for (i=0; i< len;  i++) {
			sb.remove(0);
		}
		if (div1.options[div1.selectedIndex].value != 0 && div2.options[div2.selectedIndex].value != 0) {
			var emptyOption = document.createElement('option');
			emptyOption.text = "";
			emptyOption.value = 0;
			champ.add(emptyOption);
			var option = document.createElement('option');
			option.text = div1.options[div1.selectedIndex].text;
			option.value = div1.options[div1.selectedIndex].value;
			champ.add(option);
			var option = document.createElement('option');
			option.text = div2.options[div2.selectedIndex].text;
			option.value = div2.options[div2.selectedIndex].value;
			champ.add(option);
		}
	}

	function getSBValues() {
		var champ1 = document.getElementById("nfcchamp");
		var champ2 = document.getElementById("afcchamp");
		var sb = document.getElementById("sb");
		var len = sb.length;
		for (i=0; i< len;  i++) {
			sb.remove(0);
		}
		if (champ1.options[champ1.selectedIndex].value != 0 && champ2.options[champ2.selectedIndex].value != 0) {
			var emptyOption = document.createElement('option');
			emptyOption.text = "";
			emptyOption.value = 0;
			sb.add(emptyOption);
			var option = document.createElement('option');
			option.text = champ1.options[champ1.selectedIndex].text;
			option.value = champ1.options[champ1.selectedIndex].text;
			sb.add(option);
			var option = document.createElement('option');
			option.text = champ2.options[champ2.selectedIndex].text;
			option.value = champ2.options[champ2.selectedIndex].text;
			sb.add(option);
		}
	}

 	</script>
 
</head>
<body onload="initializeWC()">
	<form action="savePicks">
	<table>
		<tr><th>WC Round</th><th>Div Round</th><th>Champ</th><th>Super Bowl</th></tr>
		<tr>
		<td width=25> <select name="afcwc1" id="afcwc1" onchange="getDivValues('afc')">
		</select>
      	<td width=25><select name="afcdiv1" id="afcdiv1" onchange="getChampValues('afc')">
		</select></td>
		<td width=25><select name="afcchamp" id="afcchamp" onchange="getSBValues()">
		</select></td>
      	<td width=25><select name="sb" id="sb">
		</select></td>
		</tr>

		<tr>
		<td width=25> <select name="afcwc2" id="afcwc2" onchange="getDivValues('afc')">
		</select>
      	<td width=25><select name="afcdiv2" id="afcdiv2" onchange="getChampValues('afc')">
		</select></td>
		<td></td>
      	<td></td>
		</tr>

		<tr>
		<td width=25> <select name="nfcwc1" id="nfcwc1" onchange="getDivValues('nfc')">
		</select></td>
      	<td width=25><select name="nfcdiv1" id="nfcdiv1" onchange="getChampValues('nfc')">
		</select></td>
		<td width=25><select name="nfcchamp" id="nfcchamp" onchange="getSBValues()">
		</select></td>
      	<td></td>
		</tr>

		<tr>
		<td width=25> <select name="nfcwc2" id="nfcwc2" onchange="getDivValues('nfc')">
		</select>
      	<td width=25><select name="nfcdiv2" id="nfcdiv2" onchange="getChampValues('nfc')">
		</select></td>
		<td></td>
      	<td></td>
		</tr>

	</table>
  	<c:if test="${!sessionScope.readOnly}">
  		<input type="submit" value="Make Picks"/>
  	</c:if>
  	</form>
	</body>
</html>