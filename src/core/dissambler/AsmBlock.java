package core.dissambler;

import java.util.ArrayList;

import core.dissambler.model.AsmInst;

public class AsmBlock {
	private int bNo; 
	private ArrayList<AsmBlock> preBlockSet;
	private ArrayList<AsmBlock> subBlockSet;
	private ArrayList<AsmInst> instList;
	
	

	public int getbNo() {
		return bNo;
	}

	public void setbNo(int bNo) {
		this.bNo = bNo;
	}

	public AsmBlock(){
		preBlockSet= new ArrayList<AsmBlock>();
		subBlockSet= new ArrayList<AsmBlock>();
		instList = new ArrayList<AsmInst>();
	}
	
	
	public void addPreBlockIndex(AsmBlock temp){
		preBlockSet.add(temp);
	}
	
	public void addSubBlockIndex(AsmBlock temp){
		subBlockSet.add(temp);
	}
	
	public void addInstModel(AsmInst temp){
		instList.add(temp);
	}

	public ArrayList<AsmBlock> getPreBlockSet() {
		return preBlockSet;
	}


	public void setPreBlockSet(ArrayList<AsmBlock> preBlockSet) {
		this.preBlockSet = preBlockSet;
	}


	public ArrayList<AsmBlock> getSubBlockSet() {
		return subBlockSet;
	}


	public void setSubBlockSet(ArrayList<AsmBlock> subBlockSet) {
		this.subBlockSet = subBlockSet;
	}

	public ArrayList<AsmInst> getInstList() {
		return instList;
	}

	public void setInstList(ArrayList<AsmInst> instList) {
		this.instList = instList;
	}
	
	public String getbNoStr(){
		return ""+bNo;
	}
	
	public String getInstListStr(){
		String ins="";
		for(AsmInst i:instList){
			ins+=ins.toString()+'\n';
		}
		return ins;
	}
	
}
