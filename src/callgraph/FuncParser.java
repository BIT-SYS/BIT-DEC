package callgraph;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;






import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import utils.FileTools;
import utils.PathTools;
import view.CallGraph;


public class FuncParser {

	public static ArrayList<FuncModel> asmfuncList;
	
	private ArrayList<FuncModel> javafuncList;
	private String projectPath;
	private String armRootPath;
	private String javaRootPath;
	private String packName;
	private String className;
	private String smaliPath;
	
	public final static String[] filterFuncs= {"__gnu_","_Unwind_","__aeabi_","__restore_"};
	/**
	 * ���������в�����
	 * @param args
	 */
	public static void main(String[] args){
		String projectPath = "C:\\Users\\zxs\\runtime-BIT_DEC.application\\test";
		FuncParser funcParser = new FuncParser(projectPath);
		funcParser.parse();
	}
	
	public FuncParser(String projectPath){
		this.smaliPath = projectPath+"/smali1/smali";
		this.projectPath = projectPath;
		this.javafuncList = new ArrayList<>();
		this.asmfuncList = new ArrayList<>();
		//��smali�ļ�����Ԥ����ȥע��
//		try {
//			this.clearComments(this.smaliPath);
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	}
	
	public void parse(){
		String armRootPath = checkARMRoot(this.projectPath);
		if (armRootPath != null) {
			this.armRootPath = armRootPath;
			//ֱ�ӻ�ȡasm�ĺ������ù�ϵ��������so����java��
			parseASMDire(new File(this.armRootPath));
		}
		System.out.println("========================================================");
		//��ȡjava�ĺ����б�
		JavaFuncList javaFuncList = new JavaFuncList();
		javaFuncList.listJavaFile(new File(this.projectPath+"/src"));
		//��ȡjava�ĺ������ñ�
		JavaCalledList javaCalledList = new JavaCalledList();
		javaCalledList.listJavaFile(new File(this.projectPath+"/src"));
		
		
		//ͨ��smali����java�ĺ�������
		//parseSmaliDire(new File(this.smaliPath));
	}
	/**
	 * ����Smali�ļ���
	 * @throws IOException 
	 * @throws ParseException 
	 */
//	private void parseSmaliDire(File file) {
//		File[] files = file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isFile()) {
//				if(files[i].getName().endsWith(".java") && !files[i].getName().contains("$")){
//					this.parseSmali(files[i]);
//				}
//			}else if (!files[i].isFile()
//					//����android�ļ����µ�smali���н���
//					&& !files[i].getName().equals("android")) {
//				parseSmaliDire(files[i]);
//			} 
//		}
//	}
	/**
	 * ����ASM�ļ���
	 */
	private void parseASMDire(File file){
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if(files[i].getName().endsWith(".asm")){
					this.parseASM(files[i]);
				}
			} else {
				parseASMDire(files[i]);
			}
		}
	}
	/**
	 * ����һ��ASM�ļ�
	 * @param file
	 */
	@SuppressWarnings("resource")
	private void parseASM(File file){
		try {
			int GoalFuncIndex = 0;
			String line = null;
			String temp5;
			HashSet<FuncModel> calledFuncList = null;
			FuncModel funcModel = null;
			InputStream is = new FileInputStream(file.getAbsolutePath());
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			while ((line = reader.readLine())!= null && !line.contains("Disassembly of section .text:"));
			while ((line = reader.readLine())!= null && !line.contains("Disassembly of section")) {
				if(line.contains(">:")){
					//�洢��һ�������ĵ��ù�ϵ
					if (funcModel != null && calledFuncList !=null ) {
						funcModel.setCalledFuncList(calledFuncList);
						asmfuncList.add(funcModel);
						System.out.println(funcModel.toString2());
					}
					//��ȡ������
					temp5 = line.substring(line.indexOf('<')+1, line.indexOf('>'));
					//���˵�û�õĺ���
					int filterFlag = 0;
					for(String fiterFunc:filterFuncs){
						if (temp5.startsWith(fiterFunc)) {
							filterFlag = 1;
							break;
						}
					}
					if (filterFlag == 1) {
						continue;
					}
					//��ʼ����ǰ����
					calledFuncList = new HashSet<FuncModel>();
					funcModel = new FuncModel();
					funcModel.setFuncName(temp5);
					GoalFuncIndex = 1;
					continue;
				}
				//��ȡ���ú���
				if(GoalFuncIndex == 1){
					String calledFuncName;
					String[] words = line.split("\\s+");
					for(int i=0;i<words.length;i++){
						if(words[i].equals("pop")){
							GoalFuncIndex = 0;
							break;
						}
						if((words[i].equals("bl")) || (words[i].equals("blx"))){
							if(line.contains("<") && line.contains(">")){
								calledFuncName = line.substring(line.indexOf('<')+1, line.indexOf('>'));
								if(calledFuncName.contains("+")){
									calledFuncName = calledFuncName.substring(0, calledFuncName.lastIndexOf('+'));
								}
								else if(calledFuncName.contains("-")){
									calledFuncName = calledFuncName.substring(0, calledFuncName.lastIndexOf('-'));
								}
								calledFuncList.add(new FuncModel(calledFuncName));
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * ����һ��smali�ļ�
	 * @param file
	 */
//	private void parseSmali(File file) {
//		BufferedReader reader = null;
//		try {
//			InputStream is = new FileInputStream(file.getAbsolutePath());
//			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//			String line = reader.readLine().trim();
//			String[] classLineStrArr;
//			StringBuffer buffer = new StringBuffer();
//			while (line != null && (!line.contains("}"))) {
//				buffer.append(line);
//				buffer.append("\r\n");
//				if (line.contains("(") && line.contains(")")) {
//					line.trim();
//					FuncModel funcModel = splitFunStr(line);
//					javafuncList.add(funcModel);
//				}
//				// �õ������Ͱ���
//				else if (line.contains("class ")) {
//					classLineStrArr = line.split("\\s+");
//					for (int i = 0; i < classLineStrArr.length; i++) {
//						if (classLineStrArr[i].equals("class")) {
//							String fullClassName = classLineStrArr[i + 1].trim();
//							int dotIndex = fullClassName.lastIndexOf(".");
//							if (dotIndex == -1)
//								this.className = fullClassName;
//							else{
//								if (this.packName == null || this.packName.equals(""))
//									this.packName = fullClassName.substring(0,dotIndex).trim();
//								this.className = fullClassName.substring(dotIndex+1).trim();
//							}
//							break;
//						}
//					}
//				}
//				// ��ð���
//				else if (line.contains("import ")) {
//					this.packName = line.substring(line.indexOf(" "),line.length()-line.indexOf(" ")).trim();
//				}
//				line = reader.readLine().trim();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				reader.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * ����һ��java����
	 * @param funcLine
	 * @return
	 */
//	private FuncModel splitFunStr(String funcLine) {
//		FuncModel funcModel = new FuncModel();
//		String[] listt = funcLine.split("[\\(\\)]");
//		String[] list1 = listt[0].split("\\s+");
//		funcModel.setPackName(this.packName);
//		//��ȡ����
//		if (list1[list1.length - 1].equals("startActivity")) {// ????????????????????��ֹһ��startActivity
//			funcModel.setClassName("");
//		} else {
//			funcModel.setClassName(this.className);
//		}
//		//��ȡ������replace
//		funcModel.setFuncName(list1[list1.length - 1].replaceAll("\\s", ""));
//		//��ȡ��������ֵ
//		if (list1.length == 1) {
//			funcModel.setRetType("");
//		} else if ((list1[list1.length - 2].equals("public"))
//				|| (list1[list1.length - 2].equals("private"))
//				|| (list1[list1.length - 2].equals("protected"))
//				|| (list1[list1.length - 2].equals("abstract"))
//				|| (list1[list1.length - 2].equals("interface"))
//				|| (list1[list1.length - 2].equals("static"))
//				|| (list1[list1.length - 2] == "final")){
//			funcModel.setRetType("");
//		} else {
//			funcModel.setRetType(list1[list1.length - 2].trim());
//		}
//		//��ȡ���������б�
//		//1.û�в��������
//		if (listt.length == 1) {
//			funcModel.setArgTypeList(new ArrayList<String>());
//		} else if (listt[1].equals("")) {
//			funcModel.setArgTypeList(new ArrayList<String>());
//		//2.���в��������
//		} else {
//			ArrayList<String> arglist = new ArrayList<>();
//			String[] list2 = listt[1].trim().split(",");
//			for (int i = 0; i < list2.length; i++) {
//				list2[i] = list2[i].trim();
//				int spaceIndex = list2[i].lastIndexOf(" ");
//				arglist.add(list2[i].substring(0, spaceIndex+1).trim());
//			}
//			funcModel.setArgTypeList(arglist);
//		}
//		//System.out.println(funcModel.toString());
//		return funcModel;
//	}
	
	/**
	 * �z���Л]��ARM�ļ�
	 * @param preojectPath �x���Ŀ��·��
	 * @return ��ձ����]��ARM�ļ�,��t����ARMRootPath
	 */
	private String checkARMRoot(String preojectPath){
		File projectFile = new File(preojectPath);
		File[] files  = projectFile.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().equals("so2asm")){
				return files[i].getAbsolutePath();
			}
		}
		return null;
	}
	/**
	 * java�ļ���ȥ��ע��
	 * @param rootPath
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private void clearComments(String rootPath) throws FileNotFoundException,
		UnsupportedEncodingException {
		File folder = new File(rootPath);
		if (folder.isDirectory()) {
			String[] files = folder.list();
			for (int i = 0; i < files.length; i++) {
				File file = new File(folder, files[i]);
				if (file.isDirectory() && file.isHidden() == false) {
					clearComments(file.getPath());
				} else if (file.isFile()) {
					FileTools.clearComment(file.getPath());
				}
			}
		} else if (folder.isFile()) {
			FileTools.clearComment(folder.getPath());
		}
	}
	
	/**
	 * ��ӡ�����б�
	 */
	public void printFuncList(){
		for(FuncModel fm: this.javafuncList){
			System.out.println(fm.toString());
		}
	}
	
	
	
	
//	private FilePretreatment clearcomment;
//	
//	private FuncBodyModel OnefuncInnerBean;
//	private FuncModel Single_func_self;
//	
//	public static ArrayList<FuncModel> allJavaFuncsInfo;
//	public static ArrayList<FuncBodyModel> allJavaCallGragh;
//	
//	public static ArrayList<FuncModel> allCFuncsInfo;
//	public static ArrayList<FuncBodyModel> allCCallGragh;
//	
//	public static ArrayList<String> myClassesNames;
//	
//	
//	private String classs_name;
//	public static boolean include = false;
//	
//	
//	
//	public FuncParser(String path1,boolean ff,String ARMfilepath){
//		allJavaFuncsInfo = new ArrayList<FuncModel>();
//		allJavaCallGragh = new ArrayList<FuncBodyModel>();
//		allCFuncsInfo = new ArrayList<FuncModel>();
//		allCCallGragh = new ArrayList<FuncBodyModel>();
//		myClassesNames = new ArrayList<String>();
//		include = ff;
//		File file  = new File(path1);
//		/*
//		 * ����Java�������Լ�����C������͵��ñ�
//		 */
//		System.out.println("path1:"+path1);
//		System.out.println("ARMfilepath:"+ARMfilepath);
//		listJavaFile(file);
//		
//		if(!ARMfilepath.equals("")){
//			File ARMfile = new File(ARMfilepath);
//			listARMFile(ARMfile);
//		}
//		/*
//		 * ����Java���ñ�
//		 */
//		JavaGraghInfo(file);
//		/*
//		 * ��������ͼ
//		 */
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		IViewPart viewPart;
//		try {
//			viewPart = page.showView("BIT_DEC.callgraphView");
//			callgraphView callgarphView = (callgraphView)viewPart;
//			callgarphView.drawCallGraph();
//		} catch (PartInitException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void listARMFile(File file) {
//		File[] files = file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isFile()) {
//				if(files[i].getName().endsWith(".txt")){
//					getCFuncInfoAddCallGragh(files[i]);
//				}
//			} else {
//				listARMFile(files[i]);
//			}
//		}
//	}
//	
//	
//	public void listJavaFile(File file) {
//		
//		File[] files = file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isFile()) {
//				if (files[i].getName().endsWith(".java")) {
//					getJavaFuncsInfo(files[i]);
//				} 
//			} else {
//				if(files[i].getName().equals("com")){
//					System.out.println("111111");
//				}
//				listJavaFile(files[i]);
//			}
//		}
//
//	}
//	
//	public void getCFuncInfoAddCallGragh(File file){
//		BufferedReader reader;
//		int startIndex = 0;
//		String temp5;
//		int GoalFuncIndex = 0;
//		OnefuncInnerBean = new FuncBodyModel();
//		try {
//			String line = null;
//			InputStream is;
//			
//			is = new FileInputStream(file.getAbsolutePath());
//			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
//			while ((line = reader.readLine())!= null){
//				
//				if(line.contains("Disassembly of section")){
//					if(line.contains(".text:")){
//						startIndex = 1;
//						continue;
//					}
//					else{
//						startIndex  = 0;
//						continue;
//					}
//				}
//				if(startIndex == 1){
//					if(line.equals("")){
//						allCCallGragh.add(OnefuncInnerBean);
//					}
//					if(line.contains(">:")){
//						//��ȡ������
//						temp5 = line.substring(line.indexOf('<')+1, line.indexOf('>'));
//						if((!temp5.startsWith("_")) && (!temp5.startsWith("__"))){
//							GoalFuncIndex = 1;
//							Single_func_self = new FuncModel();
//							OnefuncInnerBean = new FuncBodyModel();
//							Single_func_self.setFuncName(temp5);
//							OnefuncInnerBean.setFuncSelf(Single_func_self);
//							allCFuncsInfo.add(Single_func_self);
//						}
//						else{
//							GoalFuncIndex = 0;
//						}
//						
//					}
//					//��ȡ���ú���
//					if(GoalFuncIndex == 1){
//						String[] words;
//						String callFunc;
//						words = line.split("\\s+");
//						
//						for(int i=0;i<words.length;i++){
//							if(words[i].equals("pop")){
//								GoalFuncIndex = 0;
//								break;
//							}
//							if((words[i].equals("bl")) || (words[i].equals("blx"))){
//								if(line.contains("<") && line.contains(">")){
//									callFunc = line.substring(line.indexOf('<')+1, line.indexOf('>'));
//									if((!callFunc.startsWith("_")) && (!callFunc.startsWith("__"))){//????????
//										if(callFunc.contains("+")){
//											callFunc = callFunc.substring(0, callFunc.lastIndexOf('+'));
//										}
//										else if(callFunc.contains("-")){
//											callFunc = callFunc.substring(0, callFunc.lastIndexOf('-'));
//										}
//										OnefuncInnerBean.addCfuncInnerFuncs(callFunc);
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//	} 
//	
//	public void getJavaFuncsInfo(File file){
//
//		
//		 //�õ�����
//		 Copy(file.getPath(),file.getPath()+".copy.noblank");
//		 //ȥ������ע�� 
//		 clearcomment = new FilePretreatment(); 
//		 try {
//		 clearcomment.clearComment(file.getPath()+".copy.noblank"); }
//		 catch (FileNotFoundException e) { e.printStackTrace(); }
//		 catch (UnsupportedEncodingException e) {
//		 e.printStackTrace(); }
//		 //����java�õ�������Ϣ��
//	}
//	
//	public void JavaGraghInfo(File file){
//		
//		File[] files = file.listFiles();
//		
//		for (int i = 0; i < files.length; i++) {
//			if(files[i].getName().equals("android")){
//				continue;
//			}
//			else if (files[i].isFile()) {
//				if (files[i].getName().endsWith(".java.copy.noblank")) {
//					getJavaGragh(files[i]);
//				} 
//			} 
//			else{
//				JavaGraghInfo(files[i]);
//			}   
//		}
//		
//	}
//	
//	public void getJavaGragh(File file){
//		BufferedReader reader;
//		String long_string="";
//		String[] head_str;
//		int left_kuohao = 0;
//		int right_kuohao = 0;
//		try {
//			String line = null;
//			InputStream is;
//			is = new FileInputStream(file.getAbsolutePath());
//			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
//			String setclassStr = "";
//			
//			while ((line = reader.readLine())!= null && (!line.contains("}"))){
//				if(line.contains("class")){
//					head_str = line.split("\\s+");
//					for(int i=0; i<head_str.length ;i++){
//						if(head_str[i].equals("class")){
//							classs_name = head_str[i+1];
//							break;
//						}
//					}
//				}
//			}
//			
//			while((line = reader.readLine())!= null){
//				//��ȡ������������OnefuncInnerBean
//				if(!line.equals("")){
//				left_kuohao = 0;
//				right_kuohao = 0;
//				long_string = "";
//				OnefuncInnerBean = new FuncBodyModel();
//				Single_func_self = new FuncModel();
//				splitFunStr1(line);
//				OnefuncInnerBean.setFuncSelf(Single_func_self);
//				//������ȡ??????????????????????????????????????ͬʱ��Ҫ�ҳ��ú���������
//				while((line = reader.readLine())!= null){
//					if(line.contains("{")){
//						left_kuohao++;
//						if(left_kuohao == right_kuohao)
//							break;
//					}
//					else if(line.contains("}")){
//						right_kuohao++;
//						if(left_kuohao == right_kuohao)
//							break;
//					}
//					else{
//						long_string = long_string + line;
//					}
//				}
//				//??????????????????Ѱ��activityȻ����ת,���һ���setClass ��
//				if(long_string.contains("setClass")){
//					
//					FuncModel ff = new FuncModel();
//					FuncBodyModel ffi = new FuncBodyModel();
//					
//					setclassStr = long_string.substring(long_string.indexOf("setClass"));
//					setclassStr = setclassStr.substring(setclassStr.indexOf(',')+1, setclassStr.indexOf(')'));
//					ff.setFuncName("startActivity");
//					ffi.setFuncSelf(ff);
//					ffi.addFuncInnerFunc(new FuncModel("onCreate","void",null,setclassStr));
//					allJavaCallGragh.add(ffi);
//				}
//				else if(long_string.contains("startActivity")){
//					
//					FuncModel ff = new FuncModel();
//					FuncBodyModel ffi = new FuncBodyModel();
//					
//					setclassStr = long_string.substring(long_string.indexOf("startActivity"));
//					for(int q = 0; q < myClassesNames.size(); q++){
//						if(setclassStr.contains(","+myClassesNames.get(q))){
//							ff.setFuncName("startActivity");
//							ffi.setFuncSelf(ff);
//							ffi.addFuncInnerFunc(new FuncModel("onCreate","void",null,myClassesNames.get(q)));
//							allJavaCallGragh.add(ffi);
//							break;
//						}
//					}
//					
//				}
//				//Ѱ�ҵ��ú�����������������ر���Ϊ��c���ԵĲ����ƶϣ�
//				SeekFuncs(long_string);
//				allJavaCallGragh.add(OnefuncInnerBean);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//		
//	}
//	
//	public void SeekFuncs(String temp_str){
////		ɨ��java���õ�java
//		
//		
//		for(int i=0;i<allJavaFuncsInfo.size();i++){
////			???????????1.��ͬ����ͬ�ĺ��� 2.��ͬ������ͬ����
//			if(temp_str.contains(allJavaFuncsInfo.get(i).getFunName())){
//				OnefuncInnerBean.addFuncInnerFunc(allJavaFuncsInfo.get(i));
//			}
//		}
////		ɨ��java���õ�c
//		int CfuncIndex;
//		int CfuncKuohaoStartIndex;
//		int CfuncKuohaoEndIndex;
//		String rest;
//		String kuoInner;
//		String[] args;
//		String[] argsType;
//		for(int i=0;i<allCFuncsInfo.size();i++){
//			if(allCFuncsInfo.get(i).getFunName().startsWith("Java_")){
//				if(temp_str.contains(allCFuncsInfo.get(i).getFunName())){
////					��ȡC�����Ĳ���
//					CfuncIndex = temp_str.indexOf(allCFuncsInfo.get(i).getFunName());
//					rest = temp_str.substring(CfuncIndex);
//					CfuncKuohaoStartIndex = rest.indexOf('(');
//					CfuncKuohaoEndIndex = rest.indexOf(')');
//					kuoInner = rest.substring(CfuncKuohaoStartIndex+1, CfuncKuohaoEndIndex);
//					if(kuoInner.equals("")){
//					}
//					else{
//						args = kuoInner.split(",");
//						for(int j=0;j< args.length;j++){
//							args[j].trim();
//							argsType = args[j].split(" ");
//							allCFuncsInfo.get(i).addArgType(argsType[argsType.length-2]);
//						}
//					}
//					OnefuncInnerBean.addFuncInnerFunc(allJavaFuncsInfo.get(i));
//				}
//			}
//			
//		}
//	}
//	
//	public void splitFunStr1(String funcString){
//		int args_num = 0;
//		String[] listt = funcString.split("[\\(\\)]");
//		String[] list1 = listt[0].split("\\s+");
//		Single_func_self.setClassName(classs_name);
////		System.out.println(funcString);
//		if(listt.length == 1){
//			Single_func_self.setFuncArgNum(0);
//			Single_func_self.addArgType("_null");
////			System.out.println("��������������:0");
////			System.out.println("��������������:_null");
//		}
//		else{
//			String[] list2 = listt[1].split(",");
//			String[] list22;
//			int args_count = 0;
//			args_num= list2.length;
//			Single_func_self.setFuncArgNum(args_num);
////			System.out.println("��������������:"+args_num);
//			for(int i=0;i<list2.length;i++){
//				if(list2[i]==""){
////					System.out.println("���������Ͳ���:null");
//					Single_func_self.addArgType("_null");
//				}
//				else {
//					list2[i] = list2[i].trim();
//					list22 = list2[i].split("\\s+");
//					args_count = i+1;
//					if(list22.length>=2){
////						System.out.println("����"+args_count+":"+list22[list22.length-2]);
//						Single_func_self.addArgType(list22[list22.length-2]);
//					}
//					else{
//						Single_func_self.addArgType(list22[0]);
//					}
//				}
//			}
//		}
//		
//		
//		
//		Single_func_self.setFuncName(list1[list1.length-1]);
////		System.out.println("��������:"+list1[list1.length-1]);
//		if(list1.length == 1){
//			Single_func_self.setFuncReturn("_null");
////			System.out.println("����������:_null");
//		}
//		else if( (list1[list1.length-2].equals("public") )  ||
//				(list1[list1.length-2].equals("private") )  ||
//				(list1[list1.length-2].equals("protected") )  ||
//				(list1[list1.length-2].equals("abstract") )  ||
//				(list1[list1.length-2].equals("interface") )  ||
//				(list1[list1.length-2].equals("static") )  ||
//				(list1[list1.length-2] == "final") )
//			
//		{
////			System.out.println("����������: null");
//			Single_func_self.setFuncReturn("_null");
//		}
//		else{
////			System.out.println("����������:"+list1[list1.length-2]);
//			Single_func_self.setFuncReturn(list1[list1.length-2]);
//		}
//		
//			
//	}
//
//	public void PrintCGragh(){
//		for(int i=1;i<allCCallGragh.size();i++){
//			//System.out.println(allCCallGragh.get(i).getFuncSelf().getFunName());
//			for(int j=0;j<allCCallGragh.get(i).getCFuncInnerFuncsNum();j++){
//				//System.out.println("   +++"+allCCallGragh.get(i).getCfuncInnerFuncs().get(j));
//			}
//		}
//	}
//	
//	public void PrintJavaGragh(){
//		for(int i=0;i<allJavaCallGragh.size();i++){
//			//System.out.println("----------------------------------------------------");
//			//System.out.println(allJavaCallGragh.get(i).getFuncSelf().getFunName());
//			for(int j=0;j<allJavaCallGragh.get(i).getFuncInnerFuncsNum();j++){
//			//	System.out.println("  +++"+allJavaCallGragh.get(i).getFunInnerFuncs().get(j).getFunName());
//			}
//		}
//	}
//	
//	public void PrintFuncInfoList(){
//		for(int i=0;i<allJavaFuncsInfo.size();i++){
//		//	System.out.println(allJavaFuncsInfo.get(i).getFunName());
//		}
//	}
//	
//	public void Copy(String oldPath, String newPath) {
//		try {
//			int bytesum = 0;
//			int byteread = 0;
//			File oldfile = new File(oldPath);
//			if (oldfile.exists()) {
//				InputStream inStream = new FileInputStream(oldPath);
//				FileOutputStream fs = new FileOutputStream(newPath);
//				byte[] buffer = new byte[1444];
//				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread;
//					fs.write(buffer, 0, byteread);
//				}
//				inStream.close();
//			}
//		} catch (Exception e) {
//			System.out.println("error  ");
//			e.printStackTrace();
//		}
//	}
//	public void printJavaFuncInfo(){
//		for(int i=0;i<allJavaFuncsInfo.size();i++){
////			System.out.println("++++++++++++++++");
////			System.out.println("������"+allJavaFuncsInfo.get(i).getClassName());
////			System.out.println("��������"+allJavaFuncsInfo.get(i).getFunName());
//		}
//	}

}
