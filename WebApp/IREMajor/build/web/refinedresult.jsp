<%-- 
    Document   : refinedresult
    Created on : Apr 17, 2014, 1:28:36 AM
    Author     : sagar
--%>

<%@page import="majorserver.DiseaseRank"%>
<%@page import="java.util.ArrayList"%>
<%@page import="majorserver.IndexServer"%>
<%@page import="java.util.TreeSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
                            IndexServer is=new IndexServer();
                            StringBuilder moresym=new StringBuilder();
                            String moresymlist[]=request.getParameterValues("moresym");
                           /* for(String sym:moresymlist)
                            {
                               moresym.append(sym).append(",");
                            }*/
                            
                            TreeSet<String> remsymtree=new TreeSet<String>();
                            
                            //String remsymar[]=moresym.toString().split(",");
                            if(moresymlist!=null)
                            {
                            for(String rems:moresymlist)
                                remsymtree.add(rems.trim());
                            }
                            
                            
                            ArrayList<DiseaseRank> finalranklist=is.getFinalRank(remsymtree);
       
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

                <div class="header1">
                    <h3 data-index="3">Possible Conditions in relevance order</h3>
                    <ul class="selectable1">
                    <%
                    for(DiseaseRank dr: finalranklist){
                        %><li><div class="container"><div class="fname"><%=dr.getDisease()%></div><div class="relevance"><span class="progress" style="width:<%=dr.getRank()%>%"></span></div> <div class="percent"><%=dr.getRank()%>%</div></div>
                      </li>
                    <%}%>
                    </ul>

            </div>
         </div>
    </body>
</html>
