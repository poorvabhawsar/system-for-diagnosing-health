package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewCrawler {

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
		doc=Jsoup.connect(seed).timeout(30000).get();
		Elements alphaurlsAnchor=doc.select("#a-z-alpha a");
		
		for(Element url:alphaurlsAnchor)
			alphaurls.add("http://www.webmd.com"+url.attr("href"));
		
		/*for(String url:alphaurls)
			System.out.println(url);*/
		}catch(Exception e){
			System.out.println("ERROR in alpha urls");
		}
		
		/********************************/
		
		/********Get disease page urls from each alphaurl********/
		String href=null;
		for(String alphaurl:alphaurls)
		{
			try{
				doc=Jsoup.connect(alphaurl).timeout(30000).get();
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
		 * TYPE-2: Hyperlink to the symptoms is provided in separate box inside textArea
		 * 
		 * TYPE-3: symptoms option is available in a dropdown list. Article area is inside <div id="mainContent_area">
		 * and link to symptoms is inside <select id="guideNavDropdown" 
		 */
		/*try{
			BufferedReader br=new BufferedReader(new FileReader("/home/poorva/Desktop/IIIT-H/IRE/Major/errorurls"));
			
			String line=null;
			
			while((line=br.readLine())!=null)
			{
				diseaseUrls.add(line);
			}
			
			
		}catch(Exception e){}*/
		
		
		
		String url=diseaseUrls.pollFirst();
		
		while(url!=null)
		{
			try{
				doc=Jsoup.connect(url).timeout(30000).get();	// visit the url
				content.setLength(0);			//empty stringbuffer
				title=doc.title();				// get page title
				Elements titleContent=doc.select("title");	// get page title tag
				content.append(titleContent.toString()+"\n"); // append title tag to buffer
				
				/** TYPE-1**/
				Elements textContent=doc.select("div.article_rdr #textArea");
				content.append(textContent.html());
				
				
				/** TYPE-2**/
				textContent=doc.select("div.article_rdr #textArea a");
				for(Element symlink:textContent)
				{
					if(symlink.text().equalsIgnoreCase("Symptoms") || symlink.text().equalsIgnoreCase("Treatment"))
						if(!doneUrls.contains(symlink.attr("href")))
							diseaseUrls.add(symlink.attr("href"));
				}
				
				
				
				/**TYPE-3***/
				textContent=doc.select("#mainContent_area");
				content.append(textContent.html());
				// retrieve urls inside dropdown options
				
				Elements optionsUrl=doc.select("#guideNavDropdown option");
				if(optionsUrl.isEmpty())
					optionsUrl=doc.select("#qform1 option");
				for(Element dropdownUrl:optionsUrl)
					if(!doneUrls.contains(dropdownUrl.attr("value")))
						diseaseUrls.add(dropdownUrl.attr("value"));
				
				
				// check if current page has more than one page
				if(url.indexOf("?page=")<0)
				{
					Elements pages=doc.select("div.pagination_fmt a");
					for(Element pagelink:pages)
					{
						if(pagelink.attr("href").startsWith("?page="))
							if(!doneUrls.contains(url+pagelink.attr("href")))
								diseaseUrls.add(url+pagelink.attr("href"));
					}
				}
								
				// if content has only one line i.e. title.. store complete html page
				/*if(titleContent.toString().length()+1>=content.length())
					content.append(doc.html());*/
				title=sanitizeTitle(title);
				String filename="/media/0E485C17485BFFBF/webmdcrawl/errorpages/"+title+".html";
				File f=new File(filename);
				if(f.exists())
				{
					f.renameTo(new File(filename+dupFile));
					dupFile++;
				}
				
				out= new BufferedWriter(new FileWriter(filename));
				out.write(content.toString());
				out.close();

				
			}catch(Exception e){
					System.out.println(e.toString()+" ERROR in url "+url);
			}
			doneUrls.add(url);
			System.out.println(url);
			url=diseaseUrls.pollFirst();
		}
		
		/*********************************************************/

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
