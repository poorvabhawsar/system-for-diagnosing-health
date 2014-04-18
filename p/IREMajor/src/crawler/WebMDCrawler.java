package crawler;
//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileReader;
import java.io.FileWriter;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebMDCrawler {

	/**
	 * @param args
	 */
	static int dupFile=0;
	public static void main(String[] args) {
		
		
		TreeSet<String> alphaurls=new TreeSet<String>(); // urls to be crawled
		TreeSet<String> diseaseUrls=new TreeSet<String>(); // urls crawled
		TreeSet<String> doneUrls=new TreeSet<String>();
		String seed="http://www.webmd.com/a-to-z-guides/health-topics/default.htm"; // seed url
		
		Document doc= null;
		String title=null;
		
		BufferedWriter out=null;
		
		//proxy setting
		System.getProperties().put("http.proxyHost", "proxy.iiit.ac.in");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxySet", "true");
		
		StringBuilder content=new StringBuilder();
		
		/****** Get a-z alpha urls******/
		try{
		doc=Jsoup.connect(seed).timeout(0).get();
		Elements alphaurlsAnchor=doc.select("#a-z-alpha a");
		
		for(Element url:alphaurlsAnchor)
			alphaurls.add("http://www.webmd.com"+url.attr("href"));
		

		}catch(Exception e){
			System.out.println("ERROR in alpha urls");
		} 
		
		/********************************/
		
		/********Get disease page urls from each alphaurl********/
		String href=null;
		for(String alphaurl:alphaurls)
		{
			try{
				doc=Jsoup.connect(alphaurl).timeout(0).get();
				Elements diseaseAnchor=doc.select("#ContentPane8 a");
								
				for(Element url:diseaseAnchor)
				{
					href=url.attr("href");
					if(href.startsWith("http"))
					{
						diseaseUrls.add(href);
					//	System.out.println(href);
					}
					else if(href.startsWith("/"))
					{
						diseaseUrls.add("http://www.webmd.com"+href);
					//	System.out.println("http://www.webmd.com"+href);
					}
					else
					{
						diseaseUrls.add("http://www.webmd.com/"+href);
					//	System.out.println("http://www.webmd.com/"+href);
					}
				}
			}catch(Exception e){
				System.out.println("ERROR in alpha url "+alphaurl);
			}
		} 
		

		 
		/**********************************************/
		
		/******* Visit every disease page to retrieve content ****/
		/* Page types
		 * TYPE-1. Simple text page: contain description of disease and symptoms are mentioned inside 
		 * the text not necessarily in a separate section. <div id="textArea"> contains the text. Disease name
		 * is contained in <h2> inside textArea
		 * TYPE-2: Hyperlink to the symptoms is provided in separate box inside textArea or down in the Main Content
		 * 
		 * TYPE-3: symptoms option is available in a dropdown list. Article area is inside <div id="mainContent_area">
		 * and link to symptoms is inside <select id="guideNavDropdown" 
		 */
	/*	try{
			BufferedReader br=new BufferedReader(new FileReader("/home/poorva/Desktop/IIIT-H/IRE/Major/WebMD2/atozdisease"));
			
			String line=null;
			
			while((line=br.readLine())!=null)
			{
				diseaseUrls.add(line);
			}
			
			br.close();
		}catch(Exception e){}*/
		
		
		//diseaseUrls.add("http://www.webmd.com/allergies/guide/food-allergy-intolerances");
		String url=diseaseUrls.pollFirst();
		
		while(url!=null)
		{
			if(!hasSubString(url, "slideshow") )
			{
			try{
				doc=Jsoup.connect(url).timeout(0).get();	// visit the url
				content.setLength(0);			//empty stringbuffer
				title=sanitizeTitle(doc.title().toLowerCase());				// get page title
				Elements titleContent=doc.select("title");	// get page title tag
				content.append(titleContent.toString()+"\n"); // append title tag to buffer
				
				// TYPE-1 Get all main content of current page //
				Elements textContent=doc.select("#mainContent_area");
				content.append(textContent.html());
				
				
				//TYPE-2//
				Elements LinksinContent=textContent.select("a");
				for(Element symlink:LinksinContent)
				{
					//if(hasSubString(symlink.text().toLowerCase(),"symptom")|| hasSubString(symlink.text().toLowerCase(),"treatment"))
					if(hasSubString(symlink.text().toLowerCase(),"symptom"))
						if(!doneUrls.contains(symlink.attr("href")) && !url.equalsIgnoreCase(symlink.attr("href")) && symlink.attr("href").startsWith("http://www.webmd.com"))
							diseaseUrls.add(symlink.attr("href"));
				}
				
				
				
				//TYPE-3//
				// retrieve urls inside dropdown options
				
				Elements optionsUrl=textContent.select("select[name=guideNavDropdown] option");
				if(optionsUrl.isEmpty())
					optionsUrl=textContent.select("#qform1 option");
				for(Element dropdownUrl:optionsUrl)
					//if(hasSubString(dropdownUrl.attr("value"), "symptom")||hasSubString(dropdownUrl.attr("value"), "treatment"))
					if(hasSubString(dropdownUrl.attr("value"), "symptom"))
						if(!doneUrls.contains(dropdownUrl.attr("value")) && !url.equalsIgnoreCase(dropdownUrl.attr("value")) && dropdownUrl.attr("value").startsWith("http://www.webmd.com"))
							diseaseUrls.add(dropdownUrl.attr("value"));
				
				
				// check if current page has more than one page

					//Elements pages=doc.select("div.pagination_fmt div.outline_fmt.right a");
					Elements pages=textContent.select("div.pagination_fmt div.outline_fmt.right a");
				//	Elements pages=pages1.select("a");
					for(Element pagelink:pages)
					{
						if(pagelink.attr("href").startsWith("?page="))
						{
							String newurl=url;
							if(url.indexOf("?page=")>0)
								newurl=url.substring(0, url.indexOf("?page="));
							if(!doneUrls.contains(newurl+pagelink.attr("href")))
								diseaseUrls.add(newurl+pagelink.attr("href"));
						}
						else if(pagelink.attr("href").startsWith("http://www.webmd.com"))
							if(!doneUrls.contains(pagelink.attr("href")))
								diseaseUrls.add(pagelink.attr("href"));
					}

								
				
				String filename="/media/0E485C17485BFFBF/webmdcrawl2/"+title;
				File f=new File(filename+".html");
				if(f.exists())
				{
					f.renameTo(new File(filename+dupFile+".html"));
					dupFile++;
				}
				
				out= new BufferedWriter(new FileWriter(filename+".html"));
				out.write(content.toString());
				out.close();

				
			}catch(Exception e){
					System.out.println(e.toString()+" ERROR in url "+url);
			}
			}
			doneUrls.add(url);
			System.out.println(url);
			url=diseaseUrls.pollFirst();
		}
		
		/*********************************************************/

	}
	private static boolean hasSubString(String mainString, String lookupString) {
		if(mainString.indexOf(lookupString)<0)
			return false;
		return true;
	}
	
	
	
	private static String sanitizeTitle(String title) {
		StringBuilder sb=new StringBuilder(title.toLowerCase());
		
		int i=0;
		for(i=0;i<sb.length();i++)
		{
			if(sb.charAt(i)<'a' || sb.charAt(i)>'z' )
				sb.replace(i, i+1, " ");
		}
		return sb.toString();
	}

}


