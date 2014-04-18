package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MayoClinicCrawler {

	/**
	 * @param args
	 */
	static int dupFile=0;
	public static void main(String[] args) {
		TreeSet<String> alphaurls=new TreeSet<String>(); // urls to be crawled
		TreeSet<String> navUrls=new TreeSet<String>(); // navigation crawled
		TreeSet<String> diseaseUrls=new TreeSet<String>(); // urls crawled

		String seed="http://www.mayoclinic.org/diseases-conditions"; // seed url
		
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
		Elements alphaurlsAnchor=doc.select("ol.alpha a");
		
		for(Element url:alphaurlsAnchor)
			alphaurls.add("http://www.mayoclinic.org"+url.attr("href"));
		

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
				Elements diseaseAnchor=doc.select("#index ol a	"); // change this
								
				for(Element url:diseaseAnchor)
				{
					href=url.attr("href");
					if(href.startsWith("http"))
					{
						diseaseUrls.add(href);
						//System.out.println(href);
					}
					else if(href.startsWith("/"))
					{
						diseaseUrls.add("http://www.mayoclinic.org"+href);
						//System.out.println("http://www.mayoclinic.org"+href);
					}
					else
					{
						diseaseUrls.add("http://www.mayoclinic.org/"+href);
						//System.out.println("http://www.mayoclinic.org/"+href);
					}
				}
			}catch(Exception e){
				System.out.println("ERROR in alpha url "+alphaurl);
			}
		}
		
		/**********************************************/
		
		
		/***** Get the links from left navigation menu of each disease page****/
		
		for(String dUrl:diseaseUrls)
		{
			try{
				doc=Jsoup.connect(dUrl).timeout(0).get();
				Elements atomUrls=doc.select("#leftNavigation ol a "); // change this
								
				for(Element url:atomUrls)
				{
					href=url.attr("href");
					if(href.startsWith("http"))
					{
						navUrls.add(href);
						//System.out.println(href);
					}
					else if(href.startsWith("/"))
					{
						navUrls.add("http://www.mayoclinic.org"+href);
						//System.out.println("http://www.mayoclinic.org"+href);
					}
					else
					{
						navUrls.add("http://www.mayoclinic.org/"+href);
						//System.out.println("http://www.mayoclinic.org/"+href);
					}
				}
			}catch(Exception e){
				System.out.println("ERROR in Navigation url "+dUrl);
			}
		} 
		
		/******* Visit every disease page to retrieve content ****/
	/*	try{
			BufferedReader br=new BufferedReader(new FileReader("/home/poorva/sharedworkspace/WC/src/mayo/alldiseasenavurls"));
			
			String line=null;
			
			while((line=br.readLine())!=null)
				navUrls.add(line);
			
			br.close();
		}catch(Exception e){
			System.out.println("error while reading from urlfile");
		}*/
		
		
		
		String url=navUrls.pollFirst();
		
		while(url!=null)
		{
			try{
				doc=Jsoup.connect(url).timeout(0).get();	// visit the url
				content.setLength(0);			//empty stringbuffer
				title=sanitizeTitle(doc.title().toLowerCase());				// get page title
				
				// get page title
				Elements titleContent=doc.select("title");
				content.append(titleContent.toString()+"\n");
				
				// Get the heading containing disease name
				Elements heading1=doc.select("h1");
				content.append(heading1.toString()+"\n");
				
								
				// Get main content//
				Elements textContent=doc.select("#main-content");
				content.append(textContent.html());
				
				// write content to file			
				String filename="/media/0E485C17485BFFBF/mayoclinic/"+title;
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
			
			System.out.println(url);
			url=navUrls.pollFirst(); 
		}

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
