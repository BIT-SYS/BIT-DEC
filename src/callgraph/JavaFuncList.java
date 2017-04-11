package callgraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Stack;

import utils.FileTools;


public class JavaFuncList {
	
	static ArrayList<FuncModel> funcModelList;
	private String className = "";
	private String packName = "";
	
	
	public static void main(String[] args) {
		String filePath = "C:\\Users\\zxs\\runtime-BIT_DEC.application\\test\\src\\com\\example\\myandroid\\MainActivity.java.copy";
		JavaFuncList javaFuncList = new JavaFuncList();
		javaFuncList.genFuncBean(new File(filePath));
		
		
		//javaFuncList.listJavaFile(new File(filePath));
		javaFuncList.print();
	}

	public void print() {
		for (int i = 0; i < funcModelList.size(); i++) {
			funcModelList.get(i).Print();
		}
	}
	

	public JavaFuncList() {
		funcModelList = new ArrayList<FuncModel>();
	}
	/**
	 * 
	 * @param file java/src目录文件 
	 */
	public void listJavaFile(File file) {
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().endsWith(".java")) {
				//对java原文件进行复制并预处理，去除注释
				//System.out.println(files[i].getAbsolutePath());
				String srcPath = files[i].getAbsolutePath();
				String destPath = srcPath+".copy";
				try {
					FileTools.copyFile(files[i], new File(destPath));
					FileTools.clearComment(destPath);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				genFuncBean(new File(destPath));
			} 
			//构建java函数列表还是需要andoird文件夹下的内容的
			else if (files[i].isDirectory()) {
				listJavaFile(files[i]);
			}
		}
	}

	public void genFuncBean(File file) {
		
		String line = "";
		String line_pre = "";
		InputStream is;
		BufferedReader reader;
		Stack<Character> stack = new Stack<>();

		try {
			is = new FileInputStream(file.getAbsolutePath());
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			line = reader.readLine().trim();
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
				if (line.contains(" native ")
						|| (flag = (line.contains("{") && stack.size() == 1 && line_pre
								.trim().endsWith(")")))) {
					ArrayList<String> funcInfo = new ArrayList<String>();
					FuncModel funcModel = new FuncModel();
					//包含native的一个函数申明
					if (!flag) {
						funcInfo = getFunc(line);
					//一般的函数行
					} else {
						//BUG:断行的情况考虑了却没有处理
						if (line_pre.indexOf("(") == -1) {
							continue;
						}
						funcInfo = getFunc(line_pre);
					}
					funcModel.setFuncName(funcInfo.get(0));
					funcModel.setRetType(funcInfo.get(1));
					for (int i = 2; i < funcInfo.size(); i++) {
						funcModel.addArgType(funcInfo.get(i));
					}
					funcModel.setPackName(packName);
					funcModel.setClassName(className);
					funcModelList.add(funcModel);
				}
				if (line.contains("{") && !line.contains("\"")) {
					stack.push('{');
				}
				if (line.contains("}") && !stack.empty() && !line.contains("\"")) {
					stack.pop();
				}
				line_pre = line;
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
	

	
}
