<%-- 
    Document   : index
    Created on : Apr 16, 2014, 1:43:14 PM
    Author     : sagar
--%>

<%@page import="majorserver.IndexServer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    IndexServer server=new IndexServer();
    server.loadIndex("/home/sagar/poorva/IREMajor/FinalIndex");
    %>
<html>
    <head>
        <title>Health Diagnosing System</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="./cssimages/style.css" />
    </head>
    <body>
       
    <div id="page">
		
        <div id="header">
        	<h1>Health Diagnosing System</h1>
            <h2>Ask your online doctor</h2>
            
      </div>
        <div id="bar">
            <ul>
                <li><a href="#"><b>Home</b></a></li>
                <li><a href="./system.jsp">Diagnosing system</a></li>
                <li><a href="#">Contact</a></li>
            </ul>
      </div>
        <div class="contentTitle"><h1>About the system</h1></div>
        <div class="contentText">
          <p class="plaintext">We serve as an online health diagnosing system, which given a set of symptoms detects the possible diseases efficiently. Health concern on your mind? See what your medical symptoms could mean, and learn about possible conditions. </p>

        </div>
          
    </div>
    </body>
</html>
