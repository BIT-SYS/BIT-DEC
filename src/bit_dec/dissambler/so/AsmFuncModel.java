package bit_dec.dissambler.so;

import java.util.ArrayList;
import java.util.HashMap;

public class AsmFuncModel {

	private String funcAddr = "";
	private String funcName = "";
	private long start;
	private long end;
	//
	private ArrayList<AsmInstModel> instList;
	private ArrayList<AsmBlockModel> blockList;
	//
	private HashMap<Long, AsmInstModel> instMap;//Long表示指令的地址
	private HashMap<Integer, AsmBlockModel> blockMap;//Integer表示基本块的编号
	
	/**
	 * Instruct
	 */
	public AsmFuncModel(){
		instList = new ArrayList<AsmInstModel>();
		blockList = new ArrayList<AsmBlockModel>();
		instMap = new HashMap<>();
		blockMap = new HashMap<>();
	}
	
	public void addInstruction(AsmInstModel temp){
		instList.add(temp);
	}
	
	public void addBlock(AsmBlockModel temp){
		blockList.add(temp);
	}

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

	public ArrayList<AsmInstModel> getInstList() {
		return instList;
	}

	public void setInstList(ArrayList<AsmInstModel> instList) {
		this.instList = instList;
	}

	public ArrayList<AsmBlockModel> getBlockList() {
		return blockList;
	}

	public void setBlockList(ArrayList<AsmBlockModel> blockList) {
		this.blockList = blockList;
	}
	public HashMap<Long, AsmInstModel> getInstMap() {
		return instMap;
	}

	public void setInstMap(HashMap<Long, AsmInstModel> instMap) {
		this.instMap = instMap;
	}

	public HashMap<Integer, AsmBlockModel> getBlockMap() {
		return blockMap;
	}

	public void setBlockMap(HashMap<Integer, AsmBlockModel> blockMap) {
		this.blockMap = blockMap;
	}
	
}

