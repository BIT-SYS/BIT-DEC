package core.disassembler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmFunc {

	private String funcAddr = "";
	private String funcName = "";
	//private String   start; //same as funcAddr
	private String   end;     //address of last instruction
	private ArrayList<AsmInst>  instList  = new ArrayList<AsmInst> ();
	private ArrayList<AsmBlock> blockList = new ArrayList<AsmBlock>();
	//private HashMap<Long,    AsmInst>  instMap  = new HashMap<>();//Long表示指令的地址
	//private HashMap<Integer, AsmBlock> blockMap = new HashMap<>();//Integer表示基本块的编号
	
	
	public AsmInst getInstByAddr(String addr){
		//the string addr may contain function name ,we don't need it.
		Matcher match = Pattern.compile("[0-9a-f]+").matcher(addr);
		if(!match.find()) return null;
		String addr1 = match.group();
		long tmp   = Long.parseLong(addr1, 16);
		long start = Long.parseLong(funcAddr, 16);
		long stop  = Long.parseLong(end, 16);
		if(tmp>stop||tmp<start)
			return null;
		for(int i = 0;i<instList.size();i++){
			AsmInst inst = instList.get(i);
			long instAddrNum = Long.parseLong(inst.getAddr(), 16);
			//maybe the addr is mid of the instruction;
			if(instAddrNum>=tmp){
				return inst;
			}
		}
		return null;
	}
	
	//////////////////////////////////////////////
	//below are normal geters  seters 
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
	/*public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}*/
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
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

