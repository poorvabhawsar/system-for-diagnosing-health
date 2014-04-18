<%-- 
    Document   : system
    Created on : Apr 16, 2014, 2:16:30 PM
    Author     : poorva
--%>

<%@page import="java.util.Iterator"%>
<%@page import="majorserver.IndexServer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="./cssimages/style.css" />
<script src="./cssimages/jquery-1.8.0.min.js"></script>
<script src="./cssimages/jquery-1.11.0.min.js"></script>
<script src="./cssimages/select2.js"></script>
<link rel="stylesheet" href="./cssimages/select2.css">

<title>Health Diagnosing System</title>

<script type="text/javascript">
   
    $(document).ready(function() { $("#bodyparts").select2({
			 maximumSelectionSize: 5,
			 allowClear: true,
			 placeholder: "Type a word"
			}); 
		});
                
function sympage()
{
        window.location='./symptoms.jsp';
}


function populateOptions(bpname)
{
    var bodyform = document.getElementById("bodyform");
    var cur = document.getElementById(bpname);
    if($.contains(bodyform, cur)){ return; }
    var bodyselect=document.createElement("select");
    bodyselect.name="bodyparts";
    bodyselect.id=bpname;
    bodyselect.multiple=true;
    
    var par = document.createElement("p");
    var part=bpname;
    par.textContent = part;
    bodyform.appendChild(par);
    
    bodyform.appendChild(bodyselect);
    
    <% 
        Iterator iter=null;
        
    %>
    switch (bpname)
    {
        case 'head':
        <%
                
                iter = IndexServer.headbodyparts.iterator();
                while (iter.hasNext()) 
                {
                    String val=(String)iter.next();
                    %>
                    var el = document.createElement("option");
                    el.textContent = '<%=val%>';
                    el.value = '<%=val%>';
                    bodyselect.appendChild(el);
                    <%}%>
                    par.textContent = "Head";
        break;
        
        case 'torso':
        <%
                
                iter = IndexServer.torsobodyparts.iterator();
                while (iter.hasNext()) 
                {
                    String val=(String)iter.next();
                    %>
                    var el = document.createElement("option");
                    el.textContent = '<%=val%>';
                    el.value = '<%=val%>';
                    bodyselect.appendChild(el);
                    <%}%>
                    par.textContent = "Torso";
        break;
        
        case 'arm':
        <%
                
                iter = IndexServer.armbodyparts.iterator();
                while (iter.hasNext()) 
                {
                    String val=(String)iter.next();
                    %>
                    var el = document.createElement("option");
                    el.textContent = '<%=val%>';
                    el.value = '<%=val%>';
                    bodyselect.appendChild(el);
                    <%}%>
                    par.textContent = "Arm";
        break;
        
        case 'leg':
        <%
                
                iter = IndexServer.legbodyparts.iterator();
                while (iter.hasNext()) 
                {
                    String val=(String)iter.next();
                    %>
                    var el = document.createElement("option");
                    el.textContent = '<%=val%>';
                    el.value = '<%=val%>';
                    bodyselect.appendChild(el);
                    <%}%>
                    par.textContent = "Leg";
        break;
        
        case 'pelvis':
        <%
                
                iter = IndexServer.pelvisbodyparts.iterator();
                while (iter.hasNext()) 
                {
                    String val=(String)iter.next();
                    %>
                    var el = document.createElement("option");
                    el.textContent = '<%=val%>';
                    el.value = '<%=val%>';
                    bodyselect.appendChild(el);
                    <%}%>
                    par.textContent = "Pelvis";
        break;
        
    }
}
</script>

<script type="text/javascript">
$(document).ready(function() { $("#words").select2({
			 maximumSelectionSize: 5,
			 allowClear: true,
			 placeholder: "Type a word"
			}); 
		});

</script>

</head>

<body >
    <div id="page">
		
        <div id="header">
        	<h1>Health Diagnosing System</h1>
                <h2>Ask your online doctor</h2>
            
	      </div>
		<div id="bar">
		    <ul>
		        <li><a href="./index.jsp">Home</a></li>
                        <li><a href="#"> <b>Diagnosing system</b></a></li>
		        <li><a href="./index.jsp">Contact</a></li>
		    </ul>
	      </div>
		<div class="contentTitle"><h1>Symptom checker</h1></div>
		
		<div class="contentText" name="contentText" id="contentText">
		  <p class="plaintext">Take the first step and see what could be causing your symptoms. </p>
                  <ul>
                    <li>Click on human body to identify body part or select from 'Others'</li>
                    <li>Select the Body part related to your symptom :</li>
                  </ul>
                <!--<canvas id="myCanvas"></canvas> -->
		<img src="./cssimages/A-Picture-Of-A-Human-Body.jpg" alt="HumanBody" usemap="#human">

		<map id="human" name="human" class="human">
                  <area shape="rect" coords="79,121,171,268" alt="torso" onClick="populateOptions('torso')">
		  <area shape="circle" coords="121,53,50" alt="head" onClick="populateOptions('head')">
		  <area shape="poly" coords="65,115,75,126,61,230,35,310,38,370,17,339,40,216,47,136,65,115" alt="lefthand" onClick="populateOptions('arm')" >
		  <area shape="poly" coords="176,117,194,129,192,177,201,213,226,337,207,368,206,306,172,186,165,127,176,117" alt="righthand" onClick="populateOptions('arm')" >
		  <area shape="poly" coords="71,259,120,284,168,259,141,292,120,319,97,295,71,259" alt="pelvis" onClick="populateOptions('pelvis')">
		  <area shape="poly" coords="115,323,108,378,96,467,99,517,101,597,93,633,66,629,78,591,58,499,65,499,65,298,115,323" alt="leftleg" onClick="populateOptions('leg')">
		  <area shape="poly" coords="177,297,177,412,178,458,183,493,165,595,177,629,146,633,142,595,143,519,146,475,125,326,177,297" alt="rightleg" onClick="populateOptions('leg')">
		</map>

                  <div id="symp" style=" position:relative;height: 800px; width: 420px;float: right;"> 
                    <form action="symptoms.jsp" id="bodyform" method="post">
                        <p> <b>Body Parts </b></p>
                            <p> Others: </p>
                            <select name="bodyparts" multiple id="words" style="width: 300px">
                            <%
                            int len=IndexServer.otherbodyparts.size();
                            Iterator iter1 = IndexServer.otherbodyparts.iterator();
                            while (iter1.hasNext()) 
                            {
                                String val=(String)iter1.next();
                                %>
				<option name='<%=val%>' value='<%=val%>'><%=val%></option>
                        	<%}%>
                            </select>
                            <!--<select name="bodyparts" multiple id="bodyparts">
                                <option value="Chest">Chest</option>
                                <option value="Heart">Heart</option>
                                <option value="Liver">Liver</option>
                                <option value="Spleen">Spleen</option>
                                <option value="Kidney">Kidney</option>
                                <option value="Esophagus">Esophagus</option>
                                <option value="Stomach">Stomach</option>
                                <option value="Pancreas">Pancreas</option>
                                <option value="Intestines">Intestines</option> 
                            </select>-->
                            <input type="submit" id="sub" name="submit" value="Next Step" style="position:relative;float:right;" class="cbutton"></input>
			</form>
                <!--  <button type="button" onclick="sympage()" style="float:right;">Next</button>-->
		</div>
    
</div>

</body>
</html>
