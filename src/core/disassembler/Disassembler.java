package core.disassembler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Global;
import core.disassembler.model.AsmBlock;
import core.disassembler.model.AsmFunc;
import core.disassembler.model.AsmInst;

public class Disassembler {
	private static final Pattern asmFuncParttern = Pattern.compile("([0-9a-z]+)\\s<(\\S+?)>:");
	//匹配指令
	private static final Pattern asmInstParttern = Pattern.compile("\\s*([0-9a-z]+):\t([0-9a-z]{4})( ?[0-9a-z]{4}| {5}) \t([a-z0-9\\.]+)?(\\s+)?([^;]+)?(\\s+)?(;.+)?");
	private HashMap<String, AsmFunc>  funcMap  = new HashMap<String, AsmFunc>();
	private HashMap<String, AsmBlock> blockMap = new HashMap<String, AsmBlock>();
	
	public Disassembler(String asmfile){
		
		//get funcMap from sofile
		genAllFuncs(asmfile);
		//mark all blocks 
		genAllBlockMark();
		//generate all blocks
		genAllFuncBlock();
		//generate control flow between all blocks
		genAllControlFlow();
	}

	public HashMap<String, AsmFunc> getFuncMap(){
		return funcMap;
	}
	
	public HashMap<String, AsmBlock> getBlockMap(){
		return blockMap;
	}
	
	//get functions from asm file
	public HashMap<String, AsmFunc> genAllFuncs(String filepath) {
		String  line      = null; //current line in filepath
		long    instIndex = -1;   //index of instruction in function
		AsmFunc func      = null;   
		AsmInst inst      = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
			//"" != null
			while ((line = reader.readLine())!= null && !line.contains("Disassembly of section .text:"));
			while ((line = reader.readLine())!= null && !line.contains("Disassembly of section")){
				//跳过空行和数据指令
				if (line.trim().equals("") || line.trim().equals("..."))
					continue;
				Matcher funcMod = asmFuncParttern.matcher(line);
				Matcher instMod = asmInstParttern.matcher(line);
				//这一行是函数名字
				if(funcMod.matches()){
					instIndex = 0;
					String funcAddr = funcMod.group(1);
					String funcName = funcMod.group(2);
					//System.out.println("函数    "+funcName+" 地址 "+funcAddr);
					func = new AsmFunc();
					func.setFuncName(funcName);
					func.setFuncAddr(funcAddr);
					if(funcMap.containsKey(funcName))
						Global.printer.println("ERROR: in "+filepath+" \ntow or more funtion have same name!!");
					funcMap.put(funcName, func);
			    }
				//这一行是指令
				else if(instMod.matches()){
					String instAddr   = instMod.group(1);
					String instBinary = instMod.group(2)+instMod.group(3).trim();
					String instOp     = instMod.group(4);
					String instArg    = instMod.group(6);
					String instMemo   = instMod.group(8);
					instAddr   = instAddr   !=null?instAddr.trim():""; 
					instBinary = instBinary !=null?instBinary.trim():"";
					instOp     = instOp     !=null?instOp.trim():"";
					instArg    = instArg    !=null?instArg.trim():"";
					instMemo   = instMemo   !=null?instMemo.trim():"";
					//System.out.printf("地址:%5s 二进制:%10s 指令:%15s 参数:%40s 注释:%50s\n",instAddr, instBinary, instOp, instArg,instMemo);
					inst = new AsmInst(instAddr, instBinary,instOp,instArg,instMemo,instIndex++);
					if(func==null){
						Global.printer.println("ERROR: in "+filepath+" \nget instrucetion without function!!\n"+line);
						return null;
					}
					func.getInstList().add(inst);
			    }
				//不能解析
				else 
					Global.printer.println("不能解析的行:"+line);
			}
			//存储最后一个函数的内容
		} catch (IOException e) {e.printStackTrace();}
		
		return funcMap;
	}
	
	
	/**
	 * 标记基本块的入口地址
	 * @param curFunc
	 */
	private void genAllBlockMark(){
		for(AsmFunc func:funcMap.values()){
			ArrayList<AsmInst> instList = func.getInstList();
			long instListSize = instList.size();
			//(1)程序的第一个语句
			AsmInst firstInstModel = instList.get(0);
			firstInstModel.setHead(true);
			for(int i=0;i<instListSize;i++){
				AsmInst instModel = instList.get(i);
				//当是跳转语句时
				if(isJumpIns(instModel)){
					//(2)紧跟在条件转移语句后面的语句 
					if(isConsJumpIns(instModel) && i<(instListSize-1)){
						AsmInst nextInstModel = instList.get(i+1);
						nextInstModel.setHead(true);
					}
					ArrayList<String> args = instModel.getArgList();
					//(3)条件转移语句或无条件转移语句的转移目标语句
					//BUG:跳转寄存器没有考虑
					//System.out.println(args.toString());
					if (args.size() > 0 && !args.get(0).contains("r")) {
						String addr = args.get(0);
						for(AsmFunc func1:funcMap.values()){
							AsmInst inst1 = func1.getInstByAddr(addr);
							if(inst1 == null) continue;
							inst1.setHead(true);
						}
					}
				}
			}
			
		}
	}
	
	/**
	 * 根据函数名划分基本块
	 * @param funcName
	 * 1、由该入口语句直到下一个入口语句（不包含下一个入口语句）之间的所有语句构成一个基本块。
	 * 2、由该入口语句到一转移语句（含该转移语句）之间的所有语句构成一个基本块；或到程序中的
	 * 停止或暂停语句（包含该停止或暂停语句）之间的语句序列组成的。
	 */
	private void genFuncBlock(AsmFunc func){
		int bNo = 0;
		AsmBlock block = null;
		ArrayList<AsmBlock> blockList = new ArrayList<>();
		for (AsmInst inst : func.getInstList()) {
			//基本块的入口语句
			if (inst.isHead()) {
				block = new AsmBlock();
				block.setFuncName(func.getFuncName());
				blockList.add(block);
				block.setbNo(bNo++);
				block.addInst(inst);
				continue;
			}
			if(block == null) {
				Global.printer.println("ERROR: in function "+func.getFuncName()+"find a instruction not belong to any block \n"+inst.toString());
				continue;
			}
			block.addInst(inst);

		}
		func.setBlockList(blockList);
	}
	
	protected void genAllFuncBlock(){
		for(AsmFunc func:funcMap.values()){
			genFuncBlock(func);
		}
		for(AsmFunc func:funcMap.values())
		for(AsmBlock block :func.getBlockList())
			blockMap.put(block.getAddr(), block);
		
	}
	
	/**
	 * 生成控制流图
	 * @param
	 * 1、基本块k在流图中的位置紧跟在基本块i之后且i的出口语句不是无条件转移或停止语句
	 * 2、基本块i的出口语句是goto(s)或者if...goto(s)且(s)是基本块k的入口语句
	 * i为k的前驱，k为i的后继 
	 */
	public void genAllControlFlow(){
		for(AsmFunc func : funcMap.values()){
			ArrayList<AsmBlock> blockList = func.getBlockList();
			long blockListSize = blockList.size();
			for(int i = 0;i<blockListSize;i++){
				AsmBlock block =blockList.get(i);
				ArrayList<AsmInst> instList = block.getInstList();
				AsmInst lastInst = instList.get(instList.size()-1);
				String lastInstOp = lastInst.getOp();
				//基本块i的最后一条语句是跳转语句
				if(isJumpIns(lastInst)){
					String jumpedAddr = lastInst.getArgList().get(0);
					for(AsmBlock tmpBlock:this.blockMap.values()){
						if ( tmpBlock.getAddr() == jumpedAddr) {
							block.addSubBlockIndex(tmpBlock);
							tmpBlock.addPreBlockIndex(block);
							break;
						}
					}
				}
				//1、基本块k在流图中的位置紧跟在基本块i之后且i的出口语句不是无条件转移或停止语句
				//BUG:没有将POP当做停止语句
				if (!isNoConsJumpIns(lastInst)) {
					if (i+1 < blockListSize) {
						AsmBlock nextBlockModel = blockList.get(i+1);
						nextBlockModel.addPreBlockIndex(block);
						block.addSubBlockIndex(nextBlockModel);
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 判断该指令是不是转移语句
	 * @param instModel
	 * @return
	 */
	private boolean isJumpIns(AsmInst instModel){
		String op = instModel.getOp();
		if (op.startsWith("blx") || op.startsWith("bl") ||
			op.startsWith("bx") || op.startsWith("b")) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是不是无条件转移指令
	 * @param instModel
	 * @return
	 */
	private boolean isNoConsJumpIns(AsmInst instModel){
		String op = instModel.getOp();
		if (op.equals("blx") || op.equals("bl") ||
			op.equals("bx") || op.equals("b")||
			op.equals("blx.n") || op.equals("bl.n") ||
			op.equals("bx.n") || op.equals("b.n")) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是不是条件转移指令
	 * @param instModel
	 * @return
	 */
	private boolean isConsJumpIns(AsmInst instModel){
		if (isJumpIns(instModel) && !isNoConsJumpIns(instModel)) {
			return true;
		}
		return false;
	}
}
