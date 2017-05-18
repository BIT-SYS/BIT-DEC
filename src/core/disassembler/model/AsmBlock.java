package core.disassembler.model;

import java.util.ArrayList;

public class AsmBlock {
	private String funcName = "";
	private int bNo; 
	private ArrayList<AsmBlock> preBlockSet;
	private ArrayList<AsmBlock> subBlockSet;
	private ArrayList<AsmInst>  instList;
	
	
	
	
	public String getAddr(){
		if(instList.size()<1) return null;
		return instList.get(0).getAddr();
	}
	
	public String getFuncName(){
		return funcName;
	}
	
	public void setFuncName(String s){
		this.funcName = s;
	}
	
	public int getbNo() {
		return bNo;
	}

	public void setbNo(int bNo) {
		this.bNo = bNo;
	}

	public AsmBlock(){
		preBlockSet= new ArrayList<AsmBlock>();
		subBlockSet= new ArrayList<AsmBlock>();
		instList =   new ArrayList<AsmInst>();
	}
	
	
	public void addPreBlockIndex(AsmBlock temp){
		preBlockSet.add(temp);
	}
	
	public void addSubBlockIndex(AsmBlock temp){
		subBlockSet.add(temp);
	}
	
	public void addInst(AsmInst temp){
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
