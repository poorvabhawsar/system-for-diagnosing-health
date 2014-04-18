package Indexer;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class WebMDIndex {
	public static TreeMap<String,TreeSet<String>> symIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> diseaseIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> bodyIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,String> conceptToPrefer=new TreeMap<String,String>();
	
	HashSet<String> symptoms=null;
	HashSet<String> bodyparts=null;
	
	public WebMDIndex()
	{
		symptoms=new HashSet<String>();
		bodyparts=new HashSet<String>();
	}
	
	public static void setMetaMapOptions(List<String> theOptions,MetaMapApi api)
	{
		if (theOptions.size() > 0)
		{
		      api.setOptions(theOptions);
		}
	}
	
	public void process(String disease,String content, MetaMapApi api) throws Exception
	{
		PrintStream out=System.out;
	    List<Result> resultList = api.processCitationsFromString(content);
	    	    
	   // for (Result result: resultList)
	    try{
	    Result result=resultList.get(0);
	    {
	      if (result != null)
	      {
	    	  out.println("Disease: "+disease);
	    	  for (Utterance utterance: result.getUtteranceList())
	    	  {
	    		  for (PCM pcm: utterance.getPCMList()) 
	    		  {
	    			  for (Mapping map: pcm.getMappingList())
	    			  {
	    				  for (Ev mapEv: map.getEvList())
	    				  {
	    					  List<String> semantictype=mapEv.getSemanticTypes();
	    					  String prefname=mapEv.getPreferredName();
	    					  if(semantictype.indexOf("sosy")>=0 || semantictype.indexOf("patf")>=0 || semantictype.indexOf("acab")>=0 || semantictype.indexOf("fndg")>=0 || semantictype.indexOf("menp")>=0||semantictype.indexOf("mobd")>=0||semantictype.indexOf("neop")>=0||semantictype.indexOf("npop")>=0||semantictype.indexOf("podg")>=0)
	    					  {
	    						  if(!prefname.equalsIgnoreCase("Symptoms"))
					    		  {
					    			  symptoms.add(prefname);
					    			  conceptToPrefer.put(mapEv.getConceptName(),prefname);
					    		  }
	    					  }
	    					  else if(semantictype.indexOf("bpoc")>=0 || semantictype.indexOf("bsog")>=0 || semantictype.indexOf("blor")>=0 || semantictype.indexOf("bdsu")>=0 || semantictype.indexOf("bdsy")>=0)
	    						  	bodyparts.add(prefname);
		
	    				  }
	    			  }
	    		  }
	    	  }
	      }
	      else
	    	  out.println("NULL result instance! ");
	    }
	    }catch(Exception e){System.out.println("Error in WebMD process method "+e.toString());}
	    
	    api.disconnect();
	  // print symptoms and bodyparts
	    
	/*    out.println("Symptoms");
	    for(String sym:symptoms)
	    	out.println(sym);
	    out.println("Body Parts");
	    for(String bp:bodyparts)
	    	out.println(bp);*/
	    
	   // populate index
	    populateIndex(disease,symptoms,bodyparts);
		
	}

	public void populateIndex(String disease, HashSet<String> symptoms,HashSet<String> bodyparts) {
		// TODO Auto-generated method stub
		try{
		// Symptom Index creation
		
		for(String sym:symptoms)
		{
			if(!symIndex.containsKey(sym))
				symIndex.put(sym, new TreeSet<String>());
			
			symIndex.get(sym).add(disease);
		}
		

		//BodyParts Index creation
		for(String bodyp:bodyparts)
		{
			if(!bodyIndex.containsKey(bodyp))
				bodyIndex.put(bodyp, new TreeSet<String>());
			
			bodyIndex.get(bodyp).add(disease);
		}
		
		// diseaseIndex creation (Reverse)
		if(!diseaseIndex.containsKey(disease))
			diseaseIndex.put(disease, new TreeSet<String>());
		
		for(String sym:symptoms)
			diseaseIndex.get(disease).add(sym);
		
		}catch(Exception e){System.out.println("Error in populating index "+e.toString());}
	}

	public String getDisease(String diseasephrase, MetaMapApi api)
	{
		// TODO Auto-generated method stub
		List<Result> resultList = api.processCitationsFromString(diseasephrase);
	    
		// for (Result result: resultList)
		try{
		Result result=resultList.get(0);
		if (result != null)
			for (Utterance utterance: result.getUtteranceList())
				for (PCM pcm: utterance.getPCMList()) 
				{//for (Mapping map: pcm.getMappingList())
						for (Ev mapEv: pcm.getCandidatesInstance().getEvList())
		    			{
							List<String> semantictype=mapEv.getSemanticTypes();
		    				String prefname=mapEv.getPreferredName();
		    				if(semantictype.indexOf("dsyn")>=0)
		    					return prefname;
		    			}
				}

		}catch(Exception e){System.out.println("WebMD error in getting disease name: "+e.toString());}
		api.disconnect();
	    return null;
	}
}
