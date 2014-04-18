/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package majorserver;

/**
 *
 * @author sagar
 */
public class DiseaseRank implements Comparable<DiseaseRank>{
    
    String disease;
    int rank;
	
    public String getDisease()
    {
        return this.disease;
    }
    
    public int getRank()
    {
        return this.rank;
    }
	public DiseaseRank(String docID, int rank)
	{
		this.disease=docID;
		this.rank=rank;
		
	}
	
	public int compareTo(DiseaseRank othernode) {
		// TODO Auto-generated method stub
		int same=0;
		same= this.rank-othernode.rank;
		if(same<0)
                    return 1;
		if(same>0)
                    return -1;
		else
                    return 0;
	}
	
    
}
