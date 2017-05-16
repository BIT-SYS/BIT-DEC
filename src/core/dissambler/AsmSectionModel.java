package core.dissambler;

import java.util.ArrayList;
import java.util.HashMap;

public class AsmSectionModel {
	
	private String name = "";
	private ArrayList<AsmFunc> funcList;
	private HashMap<String, AsmFunc> funcMap;//String 表示函数名
	private long start;
	private long end;
	
	/**
	 * Instruct
	 */
	public AsmSectionModel(){
		funcList = new ArrayList<AsmFunc>();
		funcMap = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<AsmFunc> getFuncList() {
		return funcList;
	}

	public void setFuncList(ArrayList<AsmFunc> funcList) {
		this.funcList = funcList;
	}
	
	public void addFunctionList(AsmFunc functionname){
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
	
	public HashMap<String, AsmFunc> getFuncMap() {
		return funcMap;
	}

	public void setFuncMap(HashMap<String, AsmFunc> funcMap) {
		this.funcMap = funcMap;
	}

}
