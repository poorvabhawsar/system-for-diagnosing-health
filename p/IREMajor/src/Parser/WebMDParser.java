package Parser;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import Indexer.WebMDIndex;

public class WebMDParser {

	static MetaMapApi api =null;
	//String serverhost =null;
	//int serverport,timeout;
	static List<String> theOptions=null;
	static String stopwordlist=",a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9,able,about,across,after,http,www,almost,also,am,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,It,date,have,such,did,do,does,look,make,sure,If,them,Do,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,family,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
	static TreeSet<String> stopwords=new TreeSet<String>();
	
	public static void connectToMetaServer()
	{
		api= new MetaMapApiImpl();
		String serverhost = MetaMapApi.DEFAULT_SERVER_HOST; 
		//  int serverport = MetaMapApi.DEFAULT_SERVER_PORT; 	// default port
		
		//String serverhost = "10.3.3.194";
		//String serverhost ="10.1.33.125";
		//String serverhost ="10.1.132.106";
		int serverport=8066;
	    int timeout = 0;
	    
	    api.setHost(serverhost);
	    api.setPort(serverport);
	    api.setTimeout(timeout);
	    
	    theOptions = new ArrayList<String>();
	    
	    // initialize stopwords list
	    String sw[]=stopwordlist.split(",");
	    
	    for(String w:sw)
	    	stopwords.add(w);
		
	}
	
	public static void main(String[] args) {

		WebMDIndex mi=null;
	//connect to MetaMap server
	    connectToMetaServer();
	    
	    
	 // add options in theOptions list
	    theOptions.add("-y");  // turn on Word Sense Disambiguation
	  //  theOptions.add("-K");
	  //  theOptions.add("-prune 5");
	    theOptions.add("-A");
	    theOptions.add("-u");
	    
	 // set options in metamap api object
	    WebMDIndex.setMetaMapOptions(theOptions,api);
		
		// FILE_PATH has the path to the folder of crawled data
		String FILES_PATH=args[0];
		File folder = new File(FILES_PATH);
		StringBuilder content=new StringBuilder();
		String disease=null;
		int filecount=1,numofindex=0;
		// pick files from folder extract text and send to the  parser
		//works well for mayoclinic
		//refine files
		
		/************* CODE to filter files containing valid file names *************/
	/*	for(File f:folder.listFiles())
		{
			try{
				
				mi=new WebMDIndex();
				Document doc = Jsoup.parse(f, "UTF-8");
				disease=mi.getDisease(doc.select("title").text(),api);
				System.out.println(disease);
			
				if(disease==null)
					f.delete();
				else
					f.renameTo(new File("/home/sagar/poorva/test"+f.getName()+"____"+disease));
			}catch(Exception e){System.out.println("Error in filtering cralwed files " +e.toString());}
		}*/
		
	/*************** END************************************************************/
			int resourcesind=-1;
			for(File filetobeparsed:folder.listFiles())
			{
				try{
					content.setLength(0);
					mi=new WebMDIndex();
					Document doc = Jsoup.parse(filetobeparsed, "UTF-8");
				
					//get disease and content
					disease=filetobeparsed.getName().split("____")[1].trim(); // disease name is suffixed with file
					
					content.append(doc.select("#textArea").text());
					content.append(doc.select("#textArea li").text());
					content.append(doc.select("#textArea h3").text());

					//System.out.println(content.toString());
					
					content=sanitizeAndStopWords(content);
					resourcesind=content.toString().indexOf("Resources");
					
					if(resourcesind<0)
					{
						resourcesind=content.length();
						if(resourcesind>1000)
							resourcesind=1000;
					}
					
					if(content.length()>0)
					{
						//System.out.println("Content: "+content.toString().substring(12,resourcesind)+"----END----");
						mi.process(disease,content.toString().substring(12,resourcesind), api);
					}

									
					System.out.println(filecount);
					filecount++;
					
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
	

	private static StringBuilder sanitizeAndStopWords(StringBuilder content) {
		// TODO Auto-generated method stub
		//remove all special characters;
		StringBuilder newcontent=new StringBuilder();
		
		int i=0,len=content.length();
		/*for(;i<len;i++)
			if(content.charAt(i)=='.' ||content.charAt(i)==';'||content.charAt(i)==','||content.charAt(i)=='.'||content.charAt(i)=='-'||content.charAt(i)=='('||content.charAt(i)==')'||content.charAt(i)=='?'||content.charAt(i)==':')
				continue;
			else if((content.charAt(i)<='z' && content.charAt(i)>='a') || (content.charAt(i)<='Z' && content.charAt(i)>='A'))
				continue;
			else
				content.replace(i, i+1," ");*/
		
		//remove stop words
		String allwords[]=content.toString().split(" ");
		
		for(String aword:allwords)
			if(!stopwords.contains(aword))
				newcontent.append(aword).append(" ");
		
		
		return newcontent;
	}

	public static void createIndexFiles(int mark)
	{
		try{

			StringBuilder line=new StringBuilder();
			
			File symIndexFile=new File("Index2/webmdSymptomIndex"+mark);
			symIndexFile.createNewFile();
			PrintWriter indwriter=new PrintWriter(symIndexFile);
			
			File bodyIndexFile=new File("Index2/WebmdBodyIndex"+mark);
			bodyIndexFile.createNewFile();
			
			for(String sym:WebMDIndex.symIndex.keySet())
			{
				line.setLength(0);
				line.append(sym+"@");
				for(String dis:WebMDIndex.symIndex.get(sym))
					line.append(dis+",");
				line.append("\n");
				
				indwriter.write(line.toString());
			}
			indwriter.close();
			
			indwriter=new PrintWriter(bodyIndexFile);
			for(String bdy:WebMDIndex.bodyIndex.keySet())
			{
				line.setLength(0);
				line.append(bdy+"@");
				for(String dis:WebMDIndex.bodyIndex.get(bdy))
					line.append(dis+",");
				line.append("\n");
				
				indwriter.write(line.toString());
			}
			indwriter.close();
			
			
		
			// COncept name to Preferred name file
			File conpreFile=new File("Index2/WebmdconToPrefer"+mark);
			conpreFile.createNewFile();
			indwriter=new PrintWriter(conpreFile);
			
			for(String con:WebMDIndex.conceptToPrefer.keySet())
				indwriter.write(con+"@"+WebMDIndex.conceptToPrefer.get(con)+"\n");

			indwriter.close();
			
			// diseaseIndex Creation
			File diseaseIndexFile=new File("Index2/WebmddiseaseIndex"+mark);
			diseaseIndexFile.createNewFile();
			indwriter=new PrintWriter(diseaseIndexFile);
			
			for(String dis:WebMDIndex.diseaseIndex.keySet())
				indwriter.write(dis+"@"+WebMDIndex.diseaseIndex.get(dis)+"\n");

			indwriter.close();
			
			WebMDIndex.symIndex.clear();
			WebMDIndex.diseaseIndex.clear();
			WebMDIndex.bodyIndex.clear();
			WebMDIndex.conceptToPrefer.clear();
			
		
		}catch(Exception e)
		{
			System.out.println("Error in writing index "+e.toString());
		}
	}

}
