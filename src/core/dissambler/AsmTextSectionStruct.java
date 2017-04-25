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

public class AsmTextSectionStruct {

	public static AsmSectionModel textSectionModel = new AsmSectionModel();
	
//	public static void main(String[] args){
//		String pathString = "C:\\Users\\zxs\\Desktop\\runtime-BIT_DEC.application\\aa\\so2asm\\armeabi\\libHelloWorld.asm";
//		AsmTextSectionStruct asmTextSectionStruct = new AsmTextSectionStruct(pathString);
//		asmTextSectionStruct.struct();
//	}
	
	/**
	 * 结构化text section
	 */
	public static void struct(String filepath) {
		
		String line = null;
		InputStream is;
		boolean isPop = false;//用于识别是否到了pop指令处，如果是就跳过当前函数后面的数据指令
		AsmFuncModel funcModel = null;
		AsmInstModel instModel = null;
		HashMap<String, AsmFuncModel> funcMap = new HashMap<>();
		HashMap<Long,   AsmInstModel> instMap = new HashMap<>();
		ArrayList<AsmFuncModel> funcList = new ArrayList<>();
		ArrayList<AsmInstModel> instList = new ArrayList<>();
		
		try {
			is = new FileInputStream(filepath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null&& !line.contains("Disassembly of section .text:"));
			//	System.out.println(line);
			//System.out.println(line);
			//System.out.println("==========================================================================");
			while ((line = reader.readLine())!= null && !line.contains("Disassembly of section")) {
				//System.out.println(line);
				//函数
				if (line.trim().endsWith(">:")) {
					isPop = false;
					//存储函数的结尾
					if (funcModel != null && instModel != null) {
						funcModel.setInstMap(instMap);
						funcModel.setInstList(instList);
						funcModel.setEnd(instModel.getAddr());
						funcMap.put(funcModel.getFuncName(), funcModel);
						funcList.add(funcModel);
						//清空instMap给下一个函数使用
						instMap = new HashMap<>();
						instList = new ArrayList<>();
					}
					String[] strs = line.trim().split(" ");
					long addr = Long.parseLong(strs[0], 16);
					String funcName = strs[1].substring(1,strs[1].length()-2);
					funcModel = new AsmFuncModel();
					//存储函数的起始地址和函数名
					funcModel.setStart(addr);
					funcModel.setFuncName(funcName);
					continue;
				}
				//跳过空行和数据指令
				if (line.trim().equals("") || isPop == true || line.trim().equals("..."))
					continue;
				//指令
				instModel = structInst(line);
				instMap.put(instModel.getAddr(), instModel);
				instList.add(instModel);
				if (instModel.getOp().equals("pop")) {
					isPop = true;
				}
			}
			//存储最后一个函数的内容
			funcModel.setInstMap(instMap);
			funcModel.setInstList(instList);
			funcModel.setEnd(instModel.getAddr());
			funcMap.put(funcModel.getFuncName(), funcModel);
			funcList.add(funcModel);
			//存储funcMap
			textSectionModel.setFuncMap(funcMap);
			textSectionModel.setFuncList(funcList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 结构化指令字符串
	 * @param inst
	 * @return
	 */
	private static AsmInstModel structInst(String inst){
		AsmInstModel instModel = new AsmInstModel();
		inst = inst.trim();
		//获取注释内容
		if(inst.contains(";")){
			int pos = inst.indexOf(";");
			String memo = inst.substring(pos+1).trim();
			instModel.setMemo(memo);
			inst = inst.substring(0, pos).trim();
		}
		//获取指令地址
		int pos = inst.indexOf(":");
		long addr = Long.parseLong(inst.substring(0, pos), 16);
		instModel.setAddr(addr);
		inst = inst.substring(pos+1).trim();
		//获取指令码
		pos = inst.indexOf("\t");
		/*没有操作码和操作数的情况*/
		if (pos == -1) {
			instModel.setBinary(inst);
			return instModel;
		}
		String instCode = inst.substring(0,pos).trim();
		instModel.setBinary(instCode);
		inst = inst.substring(pos+1).trim();
		//获取操作码
		if(inst.indexOf("\t")>0)
			pos = inst.indexOf("\t");
		else
			pos = inst.indexOf(" ");
		/*空指令nop的情况*/
		if (pos == -1) {
			instModel.setOp(inst);
			return instModel;
		}
		String opCode = inst.substring(0,pos);
		instModel.setOp(opCode);
		inst = inst.substring(pos+1).trim();
		//获取操作数
		ArrayList<String> argList = new ArrayList<>();
		String args[] = inst.split(",");
		/*特殊情况  blt.n	124e <Java_com_example_myandroid_MainActivity_forsentence+0x16> */
		if (args.length == 1) {
			args = args[0].split(" ");
		}
		for(int i=0;i<args.length;i++){
			argList.add(args[i].trim());
		}
		instModel.setArgList(argList);
		//System.out.println(instModel.toString());
		return instModel;
	}

}
