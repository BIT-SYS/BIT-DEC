package bit_dec.dissambler.so;

import java.util.ArrayList;

public class AsmBlockModel {
	private int bNo; //»ù±¾¿é±àºÅ
	private ArrayList<AsmBlockModel> preBlockSet;
	private ArrayList<AsmBlockModel> subBlockSet;
	private ArrayList<AsmInstModel> instList;
	
	

	public int getbNo() {
		return bNo;
	}

	public void setbNo(int bNo) {
		this.bNo = bNo;
	}

	public AsmBlockModel(){
		preBlockSet= new ArrayList<AsmBlockModel>();
		subBlockSet= new ArrayList<AsmBlockModel>();
		instList = new ArrayList<AsmInstModel>();
	}
	
	
	public void addPreBlockIndex(AsmBlockModel temp){
		preBlockSet.add(temp);
	}
	
	public void addSubBlockIndex(AsmBlockModel temp){
		subBlockSet.add(temp);
	}
	
	public void addInstModel(AsmInstModel temp){
		instList.add(temp);
	}

	public ArrayList<AsmBlockModel> getPreBlockSet() {
		return preBlockSet;
	}


	public void setPreBlockSet(ArrayList<AsmBlockModel> preBlockSet) {
		this.preBlockSet = preBlockSet;
	}


	public ArrayList<AsmBlockModel> getSubBlockSet() {
		return subBlockSet;
	}


	public void setSubBlockSet(ArrayList<AsmBlockModel> subBlockSet) {
		this.subBlockSet = subBlockSet;
	}

	public ArrayList<AsmInstModel> getInstList() {
		return instList;
	}

	public void setInstList(ArrayList<AsmInstModel> instList) {
		this.instList = instList;
	}
	
}
