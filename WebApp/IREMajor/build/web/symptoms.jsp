<%-- 
    Document   : symptoms
    Created on : Apr 16, 2014, 1:47:43 PM
    Author     : sagar
--%>

<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="majorserver.IndexServer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="./cssimages/style.css" />
<script src="./cssimages/jquery-1.11.0.min.js"></script>
<script src="./cssimages/select2.js"></script>
<link rel="stylesheet" href="./cssimages/select2.css">

<title>Health Diagnosing System</title>

<script type="text/javascript">
$(document).ready(function() { $("#words").select2({
			 maximumSelectionSize: 5,
			 allowClear: true,
			 placeholder: "Type a word"
			}); 
		});

</script>

</head>

<body>
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
                    <p class="plaintext">Please enter the symptoms you are facing:</p>


		<img src="./cssimages/A-Picture-Of-A-Human-Body.jpg" alt="HumanBody" usemap="#human">

	

		<div id="symptoms" style="height:600px;width:400px;float:right;">
                    <form action="result.jsp" method="post">
                        <p> <b>Selected body parts </b></p>
                        <% 
                            StringBuilder bplist=new StringBuilder();
                            String bodypartslist[]=request.getParameterValues("bodyparts");
                            if(bodypartslist!=null)
                            for(String bp:bodypartslist)
                            {
                               bplist.append(bp).append(";");
                            }
                             %>   
                        
                             <textarea name="bplist" readonly="true" rows="3" cols="25" style="width: 288px; height: 34px;"><%=bplist.toString()%></textarea>
                             <br>

                         <p><b>Enter search key for symptom </b></p>
			<select name="symlist" multiple id="words" style="width: 300px">
                        <%
                            int len=IndexServer.listOfSymptoms.size();
                            Iterator iter = IndexServer.listOfSymptoms.iterator();
                            while (iter.hasNext()) 
                            {
                                String val=(String)iter.next();
                                %>
			<option name='<%=val%>' value='<%=val%>'><%=val%></option>
                        <%}%>
			</select>
                        <br>
                            <p> </p>
                        <input type="submit" name="submit" value="Show Results" class="cbutton">
                    </form>
		</div>
      
</div>

</body>
</html>

