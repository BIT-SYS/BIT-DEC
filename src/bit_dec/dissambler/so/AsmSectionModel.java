package bit_dec.dissambler.so;

import java.util.ArrayList;
import java.util.HashMap;

public class AsmSectionModel {
	
	private String name = "";
	private ArrayList<AsmFuncModel> funcList;
	private HashMap<String, AsmFuncModel> funcMap;//String 表示函数名
	private long start;
	private long end;
	
	/**
	 * Instruct
	 */
	public AsmSectionModel(){
		funcList = new ArrayList<AsmFuncModel>();
		funcMap = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<AsmFuncModel> getFuncList() {
		return funcList;
	}

	public void setFuncList(ArrayList<AsmFuncModel> funcList) {
		this.funcList = funcList;
	}
	
	public void addFunctionList(AsmFuncModel functionname){
		this.funcList.add(functionname);
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}	
	
	public HashMap<String, AsmFuncModel> getFuncMap() {
		return funcMap;
	}

	public void setFuncMap(HashMap<String, AsmFuncModel> funcMap) {
		this.funcMap = funcMap;
	}

}
