package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.TreeSet;

import org.jsoup.select.Elements;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Crawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TreeSet<String> urls=new TreeSet<String>();
		TreeSet<String> doneUrls=new TreeSet<String>();
		//urls.add("http://www.webmd.com/a-to-z-guides/health-topics/default.htm");
		//urls.add("http://www.webmd.com/a-to-z-guides/health-topics/default.htm");
		urls.add("http://www.webmd.com/a-to-z-guides/achilles-tendon-problems-cause");
		Document doc= null;
		String title=null;
		
		BufferedWriter out=null;
		
		System.getProperties().put("http.proxyHost", "proxy.iiit.ac.in");
		System.getProperties().put("http.proxyPort", "8080");
		//System.getProperties().put("http.proxyUser", "someUserName");
		//System.getProperties().put("http.proxyPassword", "somePassword");
		
		System.getProperties().put("http.proxySet", "true");
		StringBuilder linksb=new StringBuilder();
		String url=urls.pollFirst();
		doneUrls.add(url);
		
		while(url!=null)
		{
			try{
				doc=Jsoup.connect(url).get();
				title=doc.title();
				out= new BufferedWriter(new FileWriter("/media/0E485C17485BFFBF/IREMajorCrawl/"+title+".html"));
				out.write(doc.html());
				out.close();
				System.out.println(url);
				
				
				// find all urls from current page Anchor tags and append to TreeSet
				Elements alinks= doc.select("a");
				for(Element link: alinks)
				{
					linksb.setLength(0);
					linksb.append(link.attr("href"));
					if(!doneUrls.contains(linksb.toString())&& !doneUrls.contains("http://www.webmd.com"+linksb.toString()))
					{
						if(linksb.length()>0 && linksb.charAt(0)=='/')
							urls.add("http://www.webmd.com"+linksb.toString());
						else if(linksb.length()>0 && linksb.indexOf("http://www.webmd.com")==0)
							urls.add(linksb.toString());
					}
				}
				
				// find all urls from current page Dropdown options and append to vector
				Elements droplinks=doc.select("div.guideDropdown_rdr select option");
			    for(Element link: droplinks)
			    {
			    	linksb.setLength(0);
					linksb.append(link.attr("value"));
					if(!doneUrls.contains(linksb.toString())&& !doneUrls.contains("http://www.webmd.com"+linksb.toString()))
					{
						if(linksb.length()>0 && linksb.charAt(0)=='/')
							urls.add("http://www.webmd.com"+linksb.toString());
						else if(linksb.length()>0 && linksb.indexOf("http://www.webmd.com")==0)
							urls.add(linksb.toString());
					}
			    }
			}catch(Exception e)
			{
				System.out.println(e.toString());
			}
			url=urls.pollFirst();
			doneUrls.add(url);
		}
		

	}

}
