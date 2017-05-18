package core.dissambler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Global;
import core.dissambler.model.AsmFunc;
import core.dissambler.model.AsmInst;

public class AsmTextSectionStruct {
	//匹配函数
	public static Pattern asmFuncParttern = Pattern.compile("([0-9a-z]+)\\s<(\\S+?)>:");
	//匹配指令
	public static Pattern asmInstParttern = Pattern.compile("\\s*([0-9a-z]+):\t([0-9a-z]{4})( ?[0-9a-z]{4}| {5}) \t([a-z0-9\\.]+)?(\\s+)?([^;]+)?(\\s+)?(;.+)?");
	
	/*public static void main(String[] args){
		String pathString = "C:\\Users\\Administrator\\Desktop\\1.asm";
		AsmTextSectionStruct.getAsmFuncs(pathString);
	}*/
	
	public static void getAsmFuncs(String filepath) {
		HashMap<String, AsmFunc> funcMap = new HashMap<>();
		//HashMap<Long,   AsmInst> instMap = new HashMap<>();
		//ArrayList<AsmFunc> funcList = new ArrayList<>(); 
		//ArrayList<AsmInst> instList = new ArrayList<>();
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
						return;
					}
					func.getInstList().add(inst);
			    }
				//不能解析
				else 
					Global.printer.println("不能解析的行:"+line);
			}
			//存储最后一个函数的内容
		} catch (IOException e) {e.printStackTrace();}
		
		//set end address to last instruction
		for(AsmFunc f: funcMap.values()){
			f.setEnd(f.getInstList().get(f.getInstList().size()-1).getAddr());
			
		}
		Global.FUNCMAP = funcMap;
	}
}
