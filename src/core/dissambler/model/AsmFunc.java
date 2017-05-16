package core.dissambler.model;

import java.util.ArrayList;
import java.util.HashMap;

import core.dissambler.AsmBlock;

public class AsmFunc {

	private String funcAddr = "";
	private String funcName = "";
	private long   start;
	private long   end;
	private ArrayList<AsmInst>  instList  = new ArrayList<AsmInst> ();
	private ArrayList<AsmBlock> blockList = new ArrayList<AsmBlock>();
	//private HashMap<Long,    AsmInst>  instMap  = new HashMap<>();//Long表示指令的地址
	//private HashMap<Integer, AsmBlock> blockMap = new HashMap<>();//Integer表示基本块的编号
	public String getFuncAddr() {
		return funcAddr;
	}
	public void setFuncAddr(String funcAddr) {
		this.funcAddr = funcAddr;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
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
	public ArrayList<AsmInst> getInstList() {
		return instList;
	}
	public void setInstList(ArrayList<AsmInst> instList) {
		this.instList = instList;
	}
	public ArrayList<AsmBlock> getBlockList() {
		return blockList;
	}
	public void setBlockList(ArrayList<AsmBlock> blockList) {
		this.blockList = blockList;
	}
	
}

