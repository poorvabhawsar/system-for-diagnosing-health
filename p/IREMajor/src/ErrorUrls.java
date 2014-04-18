import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;


public class ErrorUrls {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileReader fr=new FileReader("/home/poorva/Desktop/IIIT-H/IRE/Major/errors");
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		int start=0;
		while((line=br.readLine())!=null)
		{
			if(line.length()>=13){
			start=line.indexOf("ERROR in url ");
			System.out.println(line.substring(start+13, line.length()));}
		}
			
		
	}

}
