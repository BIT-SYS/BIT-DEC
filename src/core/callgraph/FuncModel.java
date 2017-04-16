package core.callgraph;

import java.util.ArrayList;
import java.util.HashSet;

public class FuncModel {

	private String packName ="";
	private String className ="";
	private String funcName ="";
	private String retType ="";
	private ArrayList<String> argTypeList;
	private HashSet<FuncModel> calledFuncList;
	
	public int hashCode() { 
        return 1; 
    } 
    @Override 
    public boolean equals(Object st) { 
    	FuncModel funcModel= (FuncModel) st; 
    	if (	this.funcName.equals(funcModel.getFuncName())&&
    			this.retType.equals(funcModel.getRetType())&&
    			this.argTypeList.equals(funcModel.getArgTypeList())) {
			return true;
		}
        return false; 
    } 
	
	
	public FuncModel() {
		this.argTypeList = new ArrayList<>();
		this.calledFuncList = new HashSet<FuncModel>();
	}
	
	public FuncModel(String funcName) {
		this.argTypeList = new ArrayList<>();
		this.calledFuncList = new HashSet<FuncModel>();
		this.funcName = funcName;
	}
	
	public FuncModel(String funcName,String retType,ArrayList<String> argTypeList,String className) {
		this.funcName = funcName;
		this.retType = retType;
		this.argTypeList = argTypeList;
		this.className = className;
		this.calledFuncList = new HashSet<FuncModel>();
	}
	
	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public ArrayList<String> getArgTypeList() {
		return argTypeList;
	}

	public void setArgTypeList(ArrayList<String> argTypeList) {
		this.argTypeList = argTypeList;
	}

	public String getFuncName() {
		return funcName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public HashSet<FuncModel> getCalledFuncList() {
		return calledFuncList;
	}

	public void setCalledFuncList(HashSet<FuncModel> calledFuncList) {
		this.calledFuncList = calledFuncList;
	}
	
	/**
	 * ��̬��Ӳ���
	 * @param temp
	 */
	public void addArgType(String temp){
		argTypeList.add(temp);
	}
	
	/**
	 * ��̬��ӱ����õĺ���
	 * @param funcModel
	 */
	public void addCalledFuncModel(FuncModel funcModel){
		calledFuncList.add(funcModel);
	}

	/**
	 * ��ʾ������ϸ��Ϣ
	 */
	@Override
	public String toString(){
		String res = "";
		res += "package: "+this.packName+"\n";
		res += "class: "+this.className+"\n";
		res += this.retType+" "+this.funcName+"("+this.argTypeList.toString()+")\n\n";
		return res;
	}
	
	/**
	 * ��ʾ��������Ϣ
	 * @return
	 */
	public String toString1(){
		String res = "";
		res += this.retType+" "+this.funcName+"("+this.argTypeList.toString()+")\n";
		return res;
	}
	
	/**
	 * ��ʾ�ú������õĺ���
	 * @return
	 */
	public String toString2(){
		String res = "Master:"+this.toString1();
		if (calledFuncList != null) {
			for(FuncModel fm:calledFuncList){
				res += "Slave:"+fm.toString1();
			}
		}
		return res;
	}
	/**
	 * ��ӡ������������Ϣ
	 */
	public void Print(){
		System.out.println(this.toString());
	}
}


