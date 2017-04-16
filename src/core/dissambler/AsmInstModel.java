package core.dissambler;

import java.util.ArrayList;

public class AsmInstModel {
	private long addr;
	private String binary = "";
	private String op = "";
	private ArrayList<String> argList;
	private String instLine ="";
	private long index;
	private boolean head;
	private boolean tail;
	private String memo = "";
	
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * Instruct
	 */
	public AsmInstModel(){
		argList = new ArrayList<String>();
		head = false;
		tail = false;
	}
	
	public void addArgs(String temp){
		argList.add(temp);
	}

	public long getAddr() {
		return addr;
	}

	public void setAddr(long addr) {
		this.addr = addr;
	}

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public ArrayList<String> getArgList() {
		return argList;
	}

	public void setArgList(ArrayList<String> argList) {
		this.argList = argList;
	}

	public String getInstLine() {
		return instLine;
	}

	public void setInstLine(String instLine) {
		this.instLine = instLine;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public boolean isHead() {
		return head;
	}

	public void setHead() {
		this.head = true;
	}

	public boolean isTail() {
		return tail;
	}

	public void setTail() {
		this.tail = true;
	}
	
	public String toString(){
		String inst = ""+addr+" "+binary+" "+op;
		for (int i = 0; i < argList.size(); i++) {
			inst += " "+argList.get(i);
		}
		inst += "  ;"+memo;
		return inst;
	}
	
}
