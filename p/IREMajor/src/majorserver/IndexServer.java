package majorserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexServer {
	
	public static TreeMap<String,TreeSet<String>> symIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> diseaseIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,TreeSet<String>> bodyIndex=new TreeMap<String,TreeSet<String>>();
	public static TreeMap<String,String> conceptToPrefer=new TreeMap<String,String>();
	public static HashSet<String> listOfSymptoms=null;
	public static HashSet<String> listOfBodyparts=null;
	public static HashSet<String> listOfDiseases=null;
	
	
	public void loadIndex(String path_to_indexfiles)
	{
		BufferedReader br=null;
		String line=null;
		
		try{
				/**** 1. Load SymptomIndex ********************************************************/
				//sample: Sym@diseaselist
				//Abdominal Cramps@Inflammatory bowel disease (IBD),Intestinal ischemia,
			
				//readfile
				br=new BufferedReader(new FileReader(path_to_indexfiles+"/MayoSymptomIndex"));
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
			/**** END OF SymptomIndex creation***************************************************************/
			
			
			/**** 2. Load BodyIndex files*************************************************************************/
				//sample: bodypart@diseaselist
				//Abdomen@Lead poisoning,Lipoma,Miscarriage,

				//readfile
				br=new BufferedReader(new FileReader(path_to_indexfiles+"/MayoBodyIndex"));
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
				/**** END OF BodyIndex creation***************************************************************/
				
				
				/**** 3. Load Concept name to Preferredname Index*********************************************/
					//sample:concept name@prefername
					//Abdominal swelling@Abdomen distended

					//readfile
					br=new BufferedReader(new FileReader(path_to_indexfiles+"/MayoconToPrefer"));
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
				
				/**** END OF conToPrefer index creation*******************************************************/
					
					
				/**** 4. Load diseaseToSymptom index**********************************************************/
					//sample: disease@symptomlist
					//Kyphosis@[Back Pain, Back Pain Adverse Event, Family health status, Physical findings, Stiffness]

					//readfile
					br=new BufferedReader(new FileReader(path_to_indexfiles+"/MayodiseaseIndex"));
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
			
				/**** END OF BodyIndex file creation***************************************************************/
					
					
				/**** Get all symptom names + all concept names into listOfSymptoms*************/
					listOfSymptoms=new HashSet<String>(symIndex.keySet());
					for(String con:conceptToPrefer.keySet())
						listOfSymptoms.add(con);
					
				/**** Get all bodyparts *****************/
					listOfBodyparts=new HashSet<String>(bodyIndex.keySet());
				
				/*** Get all diseases ********************/
					listOfDiseases=new HashSet<String>(diseaseIndex.keySet());
			
		}catch(Exception e){System.out.println("Error in loading index "+e.toString());}	
	}
	
	TreeSet<String> getDiseasesFromBodyparts(ArrayList<String> bodyparts)
	{
		int i=0,len=bodyparts.size();
	
		@SuppressWarnings("unchecked")
		TreeSet<String>[] diseasefrombodyparts=new TreeSet[len];
		@SuppressWarnings("unchecked")
		TreeSet<String>[] resultdisease=new TreeSet[len];
		TreeSet<String> temp=new TreeSet<String>();
		
		for(i=0;i<len;i++)
			resultdisease[i]=new TreeSet<String>();
		
		if(len==0)
			return null; // no body parts selected /****** caution at caller's side****/
		
		//get the disease TreeSet from bodyIndex for each bodypart into array diseasefrombodyparts
		i=0;
		for(String bodyp:bodyparts)
		{
			diseasefrombodyparts[i]=bodyIndex.get(bodyp);
			i++;
		}
		
		// store the intersection of all 1, 1-2, 1-2-3 ...TreeSets diseasefrombodyparts[k] into resultdisease[k]
		temp.addAll(diseasefrombodyparts[0]);
		resultdisease[0].addAll(temp);
		
		for(i=1;i<len;i++)
		{
			temp.retainAll(diseasefrombodyparts[i]);
			resultdisease[i].addAll(temp);
		}

		// return the resultdisease[k] where k is the highest index of non empty intersection
		for(i=len-1;i>=0;i--)
			if (resultdisease[i].size()>0)
				return resultdisease[i];
		
		// if above logic fails ..return the result of first bodypart
		return resultdisease[0];
	}
	
	
	
	TreeSet<String> getDiseasesFromSymptoms(ArrayList<String> symptoms)
	{
		int i=0,len=symptoms.size();
	
		@SuppressWarnings("unchecked")
		TreeSet<String>[] diseasefromsymptoms=new TreeSet[len];
		@SuppressWarnings("unchecked")
		TreeSet<String>[] resultdisease=new TreeSet[len];
		TreeSet<String> temp=new TreeSet<String>();
		ArrayList<String> refinedsymptoms=new ArrayList<String>();
		
		for(i=0;i<len;i++)
			resultdisease[i]=new TreeSet<String>();
		
		if(len==0)
			return null; // no symptoms selected /****** caution at caller's side****/
		
		//user might enter a concept name (non medical term in symptom).. convert it to preferred name
		
		
		//get the disease TreeSet from symIndex for each symptom into array diseasefromsymptoms
		i=0;
		for(String symp:symptoms)
		{
			diseasefromsymptoms[i]=symIndex.get(symp);
			i++;
		}
		
		// store the intersection of all 1, 1-2, 1-2-3 ...TreeSets diseasefrombodyparts[k] into resultdisease[k]
		temp.addAll(diseasefromsymptoms[0]);
		resultdisease[0].addAll(temp);
		
		for(i=1;i<len;i++)
		{
			temp.retainAll(diseasefromsymptoms[i]);
			resultdisease[i].addAll(temp);
		}

		// return the resultdisease[k] where k is the highest index of non empty intersection
		for(i=len-1;i>=0;i--)
			if (resultdisease[i].size()>0)
				return resultdisease[i];
		
		// if above logic fails ..return the result of first symptom
		return resultdisease[0];
	}
	
	TreeSet<String> bodyAndSymptomResult(TreeSet<String> symDiseases, TreeSet<String> bodyDiseases)
	{
		/* Perform the intersection of Sets obtained by bodyparts and symptoms
		 * if the result set is empty, return the symDisease Set
		 */
		if(symDiseases==null)
			return bodyDiseases;
		
		if(bodyDiseases==null)
			return symDiseases;
		
		TreeSet<String> result=new TreeSet<String>();
		result.addAll(symDiseases);
		
		// intersection
		result.retainAll(bodyDiseases);
		
		if(result.size()==0)
			return symDiseases;
		
		return result;
	}
	
	public static void main(String args[])
	{
		IndexServer is=new IndexServer();
		
		is.loadIndex("/home/sagar/poorva/IREMajor/MayoFinalIndex");
		
		ArrayList<String> bodyparts=new ArrayList<String>();
		ArrayList<String> symptoms=new ArrayList<String>();
		
		//bodyparts.add("Foot");
		
		//symptoms.add("Swelling");
		symptoms.add("Joint Pain Adverse Event");
		
		TreeSet<String> bodydisease=is.getDiseasesFromBodyparts(bodyparts);
		TreeSet<String> symdisease=is.getDiseasesFromSymptoms(symptoms);
		
		System.out.println(is.bodyAndSymptomResult(symdisease, bodydisease));
		
	}
	
}
