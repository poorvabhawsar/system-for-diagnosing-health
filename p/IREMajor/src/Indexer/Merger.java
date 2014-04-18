package Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class Merger {

	public static TreeMap<String,TreeSet<String>> symIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> diseaseIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> bodyIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,String> conceptToPrefer=new TreeMap<String,String>();
	
	public static void main(String[] args) {
		
		// args[0] :: path to the folder where index files that need to be merged are kept
		// args[1] :: suffix "Mayo" for mayoclinic , "WebMD" for WebMD data
		// TODO Auto-generated method stub
		//lists to hold small index files
		ArrayList<File> symtodisfiles=new ArrayList<File>();
		ArrayList<File> distosymfiles=new ArrayList<File>();
		ArrayList<File> contopreffiles=new ArrayList<File>();
		ArrayList<File> bodytodisfiles=new ArrayList<File>();
		
		String folderpath=args[0];
		String outfileprefix=args[1];
		
		File folder=new File(folderpath);
		
		for(File f:folder.listFiles())
		{
			if(f.getName().indexOf("BodyIndex")>=0)
				bodytodisfiles.add(f);
			else if(f.getName().indexOf("conToPrefer")>=0)
				contopreffiles.add(f);
			else if(f.getName().indexOf("diseaseIndex")>=0)
				distosymfiles.add(f);
			else if(f.getName().indexOf("SymptomIndex")>=0)
				symtodisfiles.add(f);
		}
		
		try
		{
			BufferedReader br=null;
			PrintWriter mergedindexfile=null;
			String line=null;
			StringBuilder outline=new StringBuilder();
			/****** Combine files*****************/
			
			/**** 1. SymptomIndex files*******/
			//sample: Sym@diseaselist
			//Abdominal Cramps@Inflammatory bowel disease (IBD),Intestinal ischemia,
			
			for(File f:symtodisfiles)
			{
				//readfile
				br=new BufferedReader(new FileReader(f));
				//read every line 
				//convert to index format
				//close the file
				while((line=br.readLine())!=null)
				{
					String symptom=line.split("@")[0].trim();
					String diseaselist=line.split("@")[1].trim();
					
					String diseases[]=diseaselist.split(",");
					
					if(!symIndex.containsKey(symptom))
						symIndex.put(symptom, new TreeSet<String>());
					
					for(String onedisease:diseases)
						symIndex.get(symptom).add(onedisease.trim());
				}
				br.close();
			}
			//all files are read . Write symIndex to output
			mergedindexfile=new PrintWriter(outfileprefix+"SymptomIndex");
			for(String sym:symIndex.keySet())
			{
				outline.setLength(0);
				outline.append(sym+"@");
				for(String dis:symIndex.get(sym))
					outline.append(dis+",");
				outline.append("\n");
				
				mergedindexfile.write(outline.toString());
			}
			mergedindexfile.close();
			/**** END OF SymptomIndex file creation*******/
			
			
			/**** 2. BodyIndex files*******/
			//sample: bodypart@diseaselist
			//Abdomen@Lead poisoning,Lipoma,Miscarriage,
			
			for(File f:bodytodisfiles)
			{
				//readfile
				br=new BufferedReader(new FileReader(f));
				//read every line 
				//convert to index format
				//close the file
				while((line=br.readLine())!=null)
				{
					String bodyp=line.split("@")[0].trim();
					String diseaselist=line.split("@")[1].trim();
					
					String diseases[]=diseaselist.split(",");
					
					if(!bodyIndex.containsKey(bodyp))
						bodyIndex.put(bodyp, new TreeSet<String>());
					
					for(String onedisease:diseases)
						bodyIndex.get(bodyp).add(onedisease.trim());
				}
				br.close();
			}
			//all files are read . Write symIndex to output
			mergedindexfile=new PrintWriter(outfileprefix+"BodyIndex");
			for(String bodyp:bodyIndex.keySet())
			{
				outline.setLength(0);
				outline.append(bodyp+"@");
				for(String dis:bodyIndex.get(bodyp))
					outline.append(dis+",");
				outline.append("\n");
				
				mergedindexfile.write(outline.toString());
			}
			mergedindexfile.close();
			/**** END OF BodyIndex file creation*******/
			
			/**** 3. Concept name to Preferredname files*******/
			//sample:concept name@prefername
			//Abdominal swelling@Abdomen distended
			
			for(File f:contopreffiles)
			{
				//readfile
				br=new BufferedReader(new FileReader(f));
				//read every line 
				//convert to index format
				//close the file
				while((line=br.readLine())!=null)
				{
					String concept=line.split("@")[0].trim();
					String prefer=line.split("@")[1].trim();
					
					conceptToPrefer.put(concept, prefer);
				}
				br.close();
			}
			//all files are read . Write symIndex to output
			mergedindexfile=new PrintWriter(outfileprefix+"conToPrefer");
			
			for(String con:conceptToPrefer.keySet())
				mergedindexfile.write(con+"@"+conceptToPrefer.get(con)+"\n");
			
			mergedindexfile.close();
			/**** END OF conToPrefer file creation*******/
			
			
			/**** 4. diseaseToSymptom files*******/
			//sample: disease@symptomlist
			//Kyphosis@[Back Pain, Back Pain Adverse Event, Family health status, Physical findings, Stiffness]
			
			for(File f:distosymfiles)
			{
				//readfile
				br=new BufferedReader(new FileReader(f));
				//read every line 
				//convert to index format
				//close the file
				while((line=br.readLine())!=null)
				{
					String disease=line.split("@")[0].trim();
					StringBuilder symlist=new StringBuilder(line.split("@")[1].trim());
					
					//symlist.deleteCharAt(0); //remove [
					//symlist.deleteCharAt(symlist.length()-1); //remove ]
					
					String symptoms[]=symlist.toString().split(",");
					
					if(!diseaseIndex.containsKey(disease))
						diseaseIndex.put(disease, new TreeSet<String>());
					
					for(String onesym:symptoms)
						diseaseIndex.get(disease).add(onesym.trim());
				}
				br.close();
			}
			//all files are read . Write symIndex to output
			mergedindexfile=new PrintWriter(outfileprefix+"diseaseIndex");
			for(String dis:diseaseIndex.keySet())
			{
				outline.setLength(0);
				outline.append(dis+"@");
				for(String sym:diseaseIndex.get(dis))
					outline.append(sym+",");
				outline.append("\n");
				
				mergedindexfile.write(outline.toString());
			}
			mergedindexfile.close();
			/**** END OF BodyIndex file creation*******/
			
		
		}catch(Exception e){System.out.println("error in merging "+e.toString());}
	}

}
