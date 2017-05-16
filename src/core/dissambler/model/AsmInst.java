package core.dissambler.model;

import java.util.ArrayList;

public class AsmInst {
	private String  addr     = null;
	private String  binary   = null;
	private String  op       = null;
	private String  arg      = null;
	private ArrayList<String> argList = new ArrayList<String>();
	private String  memo = "";
	//private String  instLine = null;
	private long    index    = -1;
	//private boolean head     = false;
	//private boolean tail     = false;
	
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
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
	public String getArg() {
		return arg;
	}
	public void setArg(String arg) {
		this.arg = arg;
	}
	public ArrayList<String> getArgList() {
		return argList;
	}
	public void setArgList(String []argList) {
		this.argList.clear();
		for(int i=0;i<argList.length;i++)
			this.argList.add(argList[i].trim());
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}	
}
