package Indexer;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

public class MayoIndex {
	
	public static TreeMap<String,TreeSet<String>> symIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> diseaseIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> bodyIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,String> conceptToPrefer=new TreeMap<String,String>();
	
	HashSet<String> symptoms=null;
	HashSet<String> bodyparts=null;
	
	public MayoIndex()
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
	    	
	    try{
	   // for (Result result: resultList) 
	    Result result=resultList.get(0);
	    {
	      if (result != null) {
	    out.println("Disease: "+disease);
		//out.println("input text: ");
	  //  out.println(" " + result.getInputText());
		/*List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevsList();
		if (aaList.size() > 0) {
		  out.println("Acronyms and Abbreviations:");
		  for (AcronymsAbbrevs e: aaList) {
		    out.println("Acronym: " + e.getAcronym());
		    out.println("Expansion: " + e.getExpansion());
		    out.println("Count list: " + e.getCountList());
		    out.println("CUI list: " + e.getCUIList());
		  }
		}*/
		/*List<Negation> negList = result.getNegationList();
		if (negList.size() > 0) {
		  out.println("Negations:");
		  for (Negation e: negList) {
		    out.println("type: " + e.getType());
		    out.print("Trigger: " + e.getTrigger() + ": [");
		    for (Position pos: e.getTriggerPositionList()) {
		      out.print(pos  + ",");
		    }
		    out.println("]");
		    out.print("ConceptPairs: [");
		    for (ConceptPair pair: e.getConceptPairList()) {
		      out.print(pair + ",");
		    }
		    out.println("]");
		    out.print("ConceptPositionList: [");
		    for (Position pos: e.getConceptPositionList()) {
		      out.print(pos + ",");
		    }
		    out.println("]");
		  }
		}*/
		for (Utterance utterance: result.getUtteranceList()) {
		/*  out.println("Utterance:");
		  out.println(" Id: " + utterance.getId());
		  out.println(" Utterance text: " + utterance.getString());
		  out.println(" Position: " + utterance.getPosition());*/

		  for (PCM pcm: utterance.getPCMList()) {
		/*    out.println("Phrase:");
		    out.println(" text: " + pcm.getPhrase().getPhraseText());
		    out.println(" Minimal Commitment Parse: " + pcm.getPhrase().getMincoManAsString());
		    out.println("Candidates:");*/
		    
		/*    for (Ev ev: pcm.getCandidatesInstance().getEvList()) {
		    if(!ev.getSemanticTypes().equals("soys"))
		    	continue;
		    	
		      out.println(" Candidate:");
		      out.println("  Score: " + ev.getScore());
		      out.println("  Concept Id: " + ev.getConceptId());
		      out.println("  Concept Name: " + ev.getConceptName());
		      out.println("  Preferred Name: " + ev.getPreferredName());
		      out.println("  Matched Words: " + ev.getMatchedWords());
		      out.println("  Semantic Types: " + ev.getSemanticTypes());
		      out.println("  MatchMap: " + ev.getMatchMap());
		      out.println("  MatchMap alt. repr.: " + ev.getMatchMapList());
		     out.println("  is Head?: " + ev.isHead());
		      out.println("  is Overmatch?: " + ev.isOvermatch());
		      out.println("  Sources: " + ev.getSources());
		      out.println("  Positional Info: " + ev.getPositionalInfo());
		      out.println("  Pruning Status: " + ev.getPruningStatus());
		      out.println("  Negation Status: " + ev.getNegationStatus());
		    } */

		 //   out.println("Mappings:");
		    for (Mapping map: pcm.getMappingList()) {
		      //out.println(" Map Score: " + map.getScore());
		      for (Ev mapEv: map.getEvList()) {
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
		/*	out.println("   Map Score: " + mapEv.getScore());
			out.println("   Map Concept Id: " + mapEv.getConceptId());
			out.println("   Map Concept Name: " + mapEv.getConceptName());
			out.println("   Map Preferred Name: " + mapEv.getPreferredName());
			out.println("   Map Matched Words: " + mapEv.getMatchedWords());
			out.println("   Map Semantic Types: " + mapEv.getSemanticTypes());
			out.println("   Map MatchMap: " + mapEv.getMatchMap());
			out.println("   Map MatchMap alt. repr.: " + mapEv.getMatchMapList());
			out.println("   is Head?: " + mapEv.isHead());
			out.println("   is Overmatch?: " + mapEv.isOvermatch());
			out.println("   Sources: " + mapEv.getSources());
			out.println("   Positional Info: " + mapEv.getPositionalInfo());
			out.println("   Pruning Status: " + mapEv.getPruningStatus());
			out.println("   Negation Status: " + mapEv.getNegationStatus());*/
			
		      }
		    }
		  }
		}
	      } else {
		out.println("NULL result instance! ");
	      }
	    }
	    }catch(Exception e){System.out.println("Error in mayo process method "+e.toString());}
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
		
		// Symptom Index creation
		try{
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

}
