package bit_dec.callgraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class JavaCalledList {
	
	
	public static ArrayList<FuncModel> javaCalledList;
	public static HashMap<String, FuncModel> javaCalledMap;
	private String className;
	private FuncModel funcModel;
	
	
	public static void main(String[] args){
		String filePath = "C:\\Users\\zxs\\runtime-BIT_DEC.application\\test\\src";
		JavaCalledList javaCalledList = new JavaCalledList();
		JavaFuncList javaFuncList = new JavaFuncList();
		javaFuncList.listJavaFile(new File(filePath));
		javaCalledList.listJavaFile(new File(filePath));
		
	}
	
	
	public JavaCalledList(){
		javaCalledList = new ArrayList<FuncModel>();
	}
	/**
	 * 构建Java的函数调用表(不包含android文件夹)
	 * @param file project+"/src"
	 */
	public void listJavaFile(File file){
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].getName().equals("android")){
				continue;
			}
			else if (files[i].isFile()) {
				if (files[i].getName().endsWith(".java.copy")) {
					getCalledFuncs(files[i]);
				} 
			} 
			else{
				listJavaFile(files[i]);
			}   
		}
	}
	
	
	public void getCalledFuncs(File file) {
		
		Stack<Character> stack = new Stack<>();
		int funcStart = 0;
		String funcBodyStr = "";
		String packName = "";
		String setclassStr="";
		String line_pre = "";
		try {
			InputStream is = new FileInputStream(file.getAbsolutePath());
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = reader.readLine();
			do {
				//获得包名
				if (line.startsWith("package ")) {
					packName = line.substring(8, line.lastIndexOf(";"));
				//获得类名
				} else if (line.contains("class") && stack.size() == 0) {
					String[] arrStrings = line.split(" ");
					for (int i = 0; i < arrStrings.length; i++) {
						if (arrStrings[i].trim().equals("class")) {
							className = arrStrings[i + 1];
							break;
						}
					}
				}
				boolean flag = false;
				//检测到函数
				if (line.contains(" native ") || 
					(flag = (line.contains("{") && stack.size() == 1 && line_pre.trim().endsWith(")")))) {
					ArrayList<String> funcInfo = new ArrayList<String>();
					funcModel = new FuncModel();
					//包含native的一个函数申明
					if (!flag) {
						funcInfo = getFunc(line);
					//一般的函数行
					} else {
						//函数体开始了
						funcStart = 1;
						funcBodyStr = "";
						funcInfo = getFunc(line_pre);
					}
					funcModel.setFuncName(funcInfo.get(0));
					funcModel.setRetType(funcInfo.get(1));
					for (int i = 2; i < funcInfo.size(); i++) {
						funcModel.addArgType(funcInfo.get(i));
					}
					funcModel.setPackName(packName);
					funcModel.setClassName(className);
					if (!flag) {
						javaCalledList.add(funcModel);
						//System.out.println(funcModel.toString2());
					}
					if (line.contains("{") && !line.contains("\"")) {
						stack.push('{');
					}
					line_pre = line;
					continue;
				}
				
				if (line.contains("{") && !line.contains("\"")) {
					stack.push('{');
				}
				if (line.contains("}") && !stack.empty() && !line.contains("\"")) {
					stack.pop();
				}
				line_pre = line;
				
				
				//获取函数体
				if (funcStart == 1 && stack.size() >= 2){
					funcBodyStr += line;
				} 
				//函数体结束
				else if (funcStart == 1 && stack.size() == 1){
					funcStart = 0;
					//对函数体进行特殊情况的判断
					//BUG:寻找activity然后跳转,而且还有setClass 等
					if(funcBodyStr.contains("setClass")){
						setclassStr = funcBodyStr.substring(funcBodyStr.indexOf("setClass"));
						setclassStr = setclassStr.substring(setclassStr.indexOf(',')+1, setclassStr.indexOf(')'));
						funcModel.setFuncName("startActivity");
						funcModel.addCalledFuncModel(new FuncModel("onCreate","void",null,setclassStr));
						javaCalledList.add(funcModel);
					}
//					else if(funcBodyStr.contains("startActivity")){
//						funcSelfBean ff = new funcSelfBean();
//						funcInnerBean ffi = new funcInnerBean();
//						setclassStr = funcBodyStr.substring(funcBodyStr.indexOf("startActivity"));
//						for(int q = 0; q < myClassesNames.size(); q++){
//							if(setclassStr.contains(","+myClassesNames.get(q))){
//								ff.setFuncName("startActivity");
//								ffi.setFuncSelf(ff);
//								ffi.addFuncInnerFunc(new funcSelfBean("onCreate","void",null,myClassesNames.get(q)));
//								allJavaCallGragh.add(ffi);
//								break;
//							}
//						}
//					}
					//寻找调用函数并分离出参数（特别是为了c语言的参数推断）
					if (!funcBodyStr.equals("")) {
						seekFuncs(funcBodyStr);
					}
					javaCalledList.add(funcModel);
					//System.out.println(funcModel.toString2());
				}
				
				
			} while ((line = reader.readLine()) != null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param line
	 * @return [0]funcName,[1]return,[2...]args
	 */
	public ArrayList<String> getFunc(String line) {
		ArrayList<String> funcInfo = new ArrayList<String>();
		int left = line.indexOf("(");
		int right = line.lastIndexOf(")");
		String[] nameReturn = line.substring(0, left).split(" ");
		funcInfo.add(nameReturn[nameReturn.length - 1]);
		if (!className.equals(nameReturn[nameReturn.length - 1])) {
			funcInfo.add(nameReturn[nameReturn.length - 2]);
		} else {
			funcInfo.add("Construct_Function");
		}
		if (right != (left + 1)) {
			String[] argInfos = line.substring(left + 1, right).split(",");
			for (String string : argInfos) {
				funcInfo.add(string.trim().substring(0,
						string.trim().indexOf(" ")));
			}
		}
		return funcInfo;
	}

	/**
	 * 获取当前函数体当中调用的java函数和c函数
	 * @param funcBodyStr 函数体字符串
	 */
	public void seekFuncs(String funcBodyStr){
		//扫面java调用的java
		if (JavaFuncList.funcModelList != null && JavaFuncList.funcModelList.size() !=0) {
			int funcSize = JavaFuncList.funcModelList.size();
			for(int i=0;i<funcSize;i++){
				//BUG:1.不同类相同的函数 2.相同函数不同参数
				if(funcBodyStr.contains(JavaFuncList.funcModelList.get(i).getFuncName())){
					funcModel.addCalledFuncModel(JavaFuncList.funcModelList.get(i));
				}
			}
		}
		//扫描java调用的So
		if (FuncParser.asmfuncList != null && FuncParser.asmfuncList.size() != 0) {
			int funcSize = FuncParser.asmfuncList.size();
			int CfuncIndex;
			String rest;
			String kuoInner;
			String[] args;
			String[] argsType;
			
			for(int i=0;i<funcSize;i++){
				String asmfuncName = FuncParser.asmfuncList.get(i).getFuncName();
				if(asmfuncName.startsWith("Java_")){
					if(funcBodyStr.contains(asmfuncName)){
						//提取C函数的参数为参数传递做准备
						CfuncIndex = funcBodyStr.indexOf(asmfuncName);
						rest = funcBodyStr.substring(CfuncIndex);
						kuoInner = rest.substring(rest.indexOf('(')+1, rest.indexOf(')')).trim();
						if(!kuoInner.equals("")){
							args = kuoInner.split(",");
							for(int j=0;j< args.length;j++){
								argsType = args[j].trim().split(" ");
								FuncParser.asmfuncList.get(i).addArgType(argsType[argsType.length-2]);
							}
						}
						funcModel.addCalledFuncModel(FuncParser.asmfuncList.get(i));
					}
				}
			}
		}
	}
}
