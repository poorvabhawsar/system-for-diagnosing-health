<%-- 
    Document   : result
    Created on : Apr 16, 2014, 6:27:49 PM
    Author     : sagar
--%>

<%@page import="majorserver.DiseaseRank"%>
<%@page import="majorserver.IndexServer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.TreeSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    // get the list of body parts and symptoms into a TreeSet
    IndexServer is=new IndexServer();
    
    ArrayList<String> bodyparts=new ArrayList<String>();
    ArrayList<String> symptoms=new ArrayList<String>();
    
    TreeSet<String> bodypartset=null;
    TreeSet<String> symptomset=null;
    TreeSet<String> result=null;
    
    StringBuilder list=new StringBuilder();
    String bodypartslist[]=request.getParameterValues("bplist");
    if(bodypartslist!=null)
    {
        for(String bp:bodypartslist)
        {
            if(bp!=null)
                list.append(bp).append(";");
        }
        String bp[]=list.toString().split(";");
        for(String b:bp)
        {
            if(b!=null)
                bodyparts.add(b.trim());
        }
    }
    System.out.println(bodyparts.toString());
    bodypartset=is.getDiseasesFromBodyparts(bodyparts);
    
    //Symptoms
    String symlist[]=request.getParameterValues("symlist");

    for(String b:symlist)
        symptoms.add(b.trim());

    symptomset=is.getDiseasesFromSymptoms(symptoms);
    result=is.bodyAndSymptomResult(symptomset, bodypartset);
    
    IndexServer.rankedresult.clear();
    for(String d:result)
        IndexServer.rankedresult.put(d, 1);
    
    // get the symptoms related to short listed diseases
    
    TreeSet<String> remainingSym=new TreeSet<String>();
    if(result.size()>1)
    {
        for(String dis:result)
        {
            remainingSym.addAll(is.getRemainingSymptoms(dis,symptoms));
        }
    }

     
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
                <li><a href="./index.jsp">Home</a></li>
                <li><a href="#"> <b>Diagnosing system</b></a></li>
		<li><a href="./index.jsp">Contact</a></li>
            </ul>
      </div>
        <div class="header1" style="float:left;">
            <h3 data-index="3">Possible Conditions</h3>
            <ul class="selectable1">
            <%
            for(String res:result){
                %><li><%=res%></li>
            <%}%>
            </ul>
        
    </div>
            <%if(remainingSym.size()>0){%>
            <form action="refinedresult.jsp" method="post">
            <div class="header2" style="float:right;">
               <h3 data-index="3">Do you have any of the following symptoms?</h3>
               
                <ul class="selectable1">
                <%
                    for(String remsym:remainingSym){
                        %><li>
                            <input type="checkbox" name="moresym" value="<%=remsym%>"><%=remsym%>
                            </li>
                    <%}%>
            </ul>
             
               
            </div>
            <input type="submit" name="submit" value="Refine Results" class="cbutton">
            </form>
            
            <%}%>
    </body>
</html>
