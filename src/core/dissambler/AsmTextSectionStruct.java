package core.dissambler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import core.dissambler.model.AsmFunc;
import core.dissambler.model.AsmInst;
import utils.Global;

public class AsmTextSectionStruct {
	//匹配函数
	public static Pattern asmFuncParttern = Pattern.compile("([0-9a-z]+)\\s<(\\S+?)>:");
	//匹配指令
	public static Pattern asmInstParttern = Pattern.compile("\\s*(\\w+):\\s+(\\w+)\\s+(\\S+)(\\s+)?(.+?)?(\\s+)?(;.+)?");
	public static AsmSection textSectionModel = new AsmSection();
	
	/*public static void main(String[] args){
		String pathString = "C:\\Users\\Administrator\\Desktop\\1.asm";
		AsmTextSectionStruct.getAsmFuncs(pathString);
	}*/
	
	public static HashMap<String, AsmFunc> getAsmFuncs(String filepath) {
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
					String instBinary = instMod.group(2);
					String instOp     = instMod.group(3);
					String instArg    = instMod.group(5);
					String instMemo   = instMod.group(7);
					//System.out.println("指令    "+instOp+" "+instArg+" 地址 "+instAddr+" 二进制 "+instBinary+" 注释 "+instMemo);
					
					//指令
					inst = new AsmInst();
					inst.setAddr(instAddr);
					inst.setBinary(instBinary);
					inst.setOp(instOp);
					inst.setArg(instArg);
					inst.setMemo(instMemo);
					inst.setArgList(instArg.split(","));
					inst.setIndex(instIndex++);
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
}
