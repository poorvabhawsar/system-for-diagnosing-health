package Parser;


import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



import Indexer.MayoIndex;


public class MayoParser {

	static MetaMapApi api =null;
	//String serverhost =null;
	//int serverport,timeout;
	static List<String> theOptions=null;
	
	
	public static void connectToMetaServer()
	{
		api= new MetaMapApiImpl();
		String serverhost = MetaMapApi.DEFAULT_SERVER_HOST; 
		//  int serverport = MetaMapApi.DEFAULT_SERVER_PORT; 	// default port
		
		//String serverhost = "10.3.3.194";
		//String serverhost ="10.1.33.125";
		int serverport=8066;
	    int timeout = 0;
	    
	    api.setHost(serverhost);
	    api.setPort(serverport);
	    api.setTimeout(timeout);
	    
	    theOptions = new ArrayList<String>();
		
	}
	
	public static void main(String[] args) {
		MayoIndex mi=null;
	//connect to MetaMap server
	    connectToMetaServer();
	    
	    
	 // add options in theOptions list
	    theOptions.add("-y");  // turn on Word Sense Disambiguation
	   // theOptions.add("-K");
	  //  theOptions.add("--prune 5");
	    theOptions.add("-A"); // Strict model
	    theOptions.add("-u"); //  Unique Acronym/Abbreviation Variants Only
	 //   theOptions.add("-J");
	    
	 // set options in metamap api object
	    MayoIndex.setMetaMapOptions(theOptions,api);
		
		// FILE_PATH has the path to the folder of crawled data
		String FILES_PATH=args[0];
		File folder = new File(FILES_PATH);
		StringBuilder content=new StringBuilder();
		String disease=null;
		int filecount=1,numofindex=0;
		
		// pick files from folder extract text and send to the  parser
		//works well for mayoclinic
		
			for(File filetobeparsed:folder.listFiles())
			{
				try{
					content.setLength(0);
					mi=new MayoIndex();
					Document doc = Jsoup.parse(filetobeparsed, "UTF-8");
					disease=doc.select("h1").text();
				
					doc.select("div.expandable.references").remove();
					doc.select("#seeAlsoList").remove();
					doc.select("productsServicesList").remove();
					
					// get li and p text
					Elements li=doc.select("li");
					content.append(li.text());
					
					Elements p=doc.select("p");
					content.append(p.text());
					
					//content.append(doc.text());
					//System.out.println("Content: "+content.toString()+"----END----");
					
					if(disease!=null && content.length()>0 && disease.length()>0)
						mi.process(disease,content.toString(), api);
					
					System.out.println(filecount);
					filecount++;
					
					// create index files if 50 files are parsed
					if(filecount%50==0)
					{
						createIndexFiles(numofindex);
						numofindex++;
					}
				
				}catch(Exception e)
				{
				System.out.println("Error while parsing "+e.toString());
				}	
			}
			if(filecount%50>0)
			{
				createIndexFiles(numofindex);
				numofindex++;
			}
	}

	public static void createIndexFiles(int mark) {
		// TODO Auto-generated method stub
		
		try{

			StringBuilder line=new StringBuilder();
			
			File symIndexFile=new File("Index/MayoSymptomIndex"+mark);
			symIndexFile.createNewFile();
			PrintWriter indwriter=new PrintWriter(symIndexFile);
			
			File bodyIndexFile=new File("Index/MayoBodyIndex"+mark);
			bodyIndexFile.createNewFile();
			
			for(String sym:MayoIndex.symIndex.keySet())
			{
				line.setLength(0);
				line.append(sym+"@");
				for(String dis:MayoIndex.symIndex.get(sym))
					line.append(dis+",");
				line.append("\n");
				
				indwriter.write(line.toString());
			}
			indwriter.close();
			
			indwriter=new PrintWriter(bodyIndexFile);
			for(String bdy:MayoIndex.bodyIndex.keySet())
			{
				line.setLength(0);
				line.append(bdy+"@");
				for(String dis:MayoIndex.bodyIndex.get(bdy))
					line.append(dis+",");
				line.append("\n");
				
				indwriter.write(line.toString());
			}
			indwriter.close();
			
			
		
			// COncept name to Preferred name file
			File conpreFile=new File("Index/MayoconToPrefer"+mark);
			conpreFile.createNewFile();
			indwriter=new PrintWriter(conpreFile);
			
			for(String con:MayoIndex.conceptToPrefer.keySet())
				indwriter.write(con+"@"+MayoIndex.conceptToPrefer.get(con)+"\n");

			indwriter.close();
			
			// diseaseIndex Creation
			File diseaseIndexFile=new File("Index/MayodiseaseIndex"+mark);
			diseaseIndexFile.createNewFile();
			indwriter=new PrintWriter(diseaseIndexFile);
			
			for(String dis:MayoIndex.diseaseIndex.keySet())
				indwriter.write(dis+"@"+MayoIndex.diseaseIndex.get(dis)+"\n");

			indwriter.close();
			
			MayoIndex.symIndex.clear();
			MayoIndex.diseaseIndex.clear();
			MayoIndex.bodyIndex.clear();
			MayoIndex.conceptToPrefer.clear();
		
		}catch(Exception e)
		{
			System.out.println("Error in writing index "+e.toString());
		}
		
	}

}
