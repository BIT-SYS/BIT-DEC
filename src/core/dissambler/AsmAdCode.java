package core.dissambler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import utils.StringTools;
import view.AdvancedCodeView;
import core.callgraph.FuncModel;
import core.callgraph.JavaCalledList;

public class AsmAdCode {

	private ArrayList<Integer> vertices;
	private ArrayList<ArrayList<Integer>> adjacentTable;
	private AsmAdCode copyedG;
	private ArrayList<nodeinfo> treeList;
	private ArrayList<VarNode> V1;
	static ArrayList<VarNode> P;
	private AdvancedCodeView myView;
	private AsmFuncModel func;
	private ArrayList<AsmBlockModel> blockList;
	private String funcHeadStr;
	private StringBuilder funcBodyStr;
	private String funcContentStr;
	
	public String cfgAna(String funcName) throws Exception {
		AsmFuncModel funcModel = AsmTextSectionStruct.textSectionModel.getFuncMap().get(funcName);
		// ================�Ժ����Ļ��������Ԥ����======================
		preDealBlocks(funcModel);
		// ================�������ͷ���===============================
		/* ���������ͣ�
		 * 1��Java_com_��ʽ
		 * 2��_Z7GetTimev(��ʱ������)
		 */
		if (!funcName.contains("Java_")) {
			throw new Exception("��ʱ�޷�����˺���");
		}
		funcName = funcName.substring(funcName.lastIndexOf("_") + 1);
		funcHeadStr = structFuncHead(funcName);
		funcBodyStr = new StringBuilder();
		//typeAnalysis1(argliststr);
		// ================��ӡ����  =======================
		//printVars();
		// ================��Լ�γ��� ======================
		reduce(this);
		// ================�߼��������� ====================
//		System.out.println("/******    �߼�����     *******/");
//		System.out.println("/*************************/");
//		funcContentStr = funcHeadStr +"\n"+"{"+funcBodyStr.toString()+"\n}";
//		System.out.println(funcContentStr);
		return funcContentStr;
	}
	
	/**
	 * �������Ļ���������ڽӱ��н���Ԥ����
	 * @param funcName
	 */
	private void preDealBlocks(AsmFuncModel funcModel){
		vertices = new ArrayList<Integer>();
		adjacentTable = new ArrayList<ArrayList<Integer>>();
		treeList = new ArrayList<nodeinfo>();
		if (funcModel == null)
			return;
		blockList = funcModel.getBlockList();
		int block_list_size = blockList.size();
		for (int i = 0; i < block_list_size; i++) {
			AsmBlockModel block = blockList.get(i);
			addVertex(block.getbNo());
			ArrayList<AsmBlockModel> subBlockSet = block.getSubBlockSet();
			int subBlockNum = subBlockSet.size();
			for (int j = 0; j < subBlockNum; j++) {
				addEdge(block.getbNo(), subBlockSet.get(j).getbNo());
			}
		}
		/* ???????????????????????????????
		 * �������ò��ûɶ��
		 */
		copyedG = makeACopy();
		printTable();
	}
	
	private void printTable(){
		System.out.println("/********�������ڽӱ�********/");
		System.out.println("/*************************/");
		for (int i = 0; i < adjacentTable.size(); i++) {
			ArrayList<Integer> row = adjacentTable.get(i);
			System.out.println(vertices.get(i)+":"+row.toString());
		}
	}
	
	/**
	 * �����߼�����ĺ���ͷ
	 * @param funcName ������
	 * @return
	 * @throws Exception 
	 */
	private String structFuncHead(String funcName) throws Exception{
		// ================��������ͷ =========================
		String headstr = "";
		if (JavaCalledList.javaCalledList == null) {
			throw new Exception("call graph operation first!");
		}
		int javafuncsSize = JavaCalledList.javaCalledList.size();
		for (int i = 0; i < javafuncsSize; i++) {
			FuncModel funcModel = JavaCalledList.javaCalledList.get(i);
			String callfuncName = funcModel.getFuncName();
			if (callfuncName.equals(funcName)) {
				String returnStr= funcModel.getRetType();
				ArrayList<String> arglist = funcModel.getArgTypeList();
				int arglistSize = arglist.size();
				headstr = returnStr + " " + funcName + " (";
				for (int k = 0; k < arglistSize; k++) {
					int k1 = k + 2;
					if (k == (arglistSize - 1)) {
						headstr = headstr + arglist.get(k) + " r" + k1;
					} else {
						headstr = headstr + arglist.get(k) + " r" + k1 + ", ";
					}
				}
				headstr +=")";
				System.out.println(headstr);
				break;
			}
		}
		return headstr;
	}
	

	/*
	 * �ж��Ƿ������call graph����
	 */
	public boolean judgeCallGraphed() {
		return true;
	}

	/*
	 * ��Լ�γ���
	 */
	public void reduce(AsmAdCode g) {
		ArrayList<Integer> visited = new ArrayList<Integer>();
		ArrayList<Integer> vlist = new ArrayList<Integer>();
		int currPos = 0;
		boolean changed = true;
		
		while(this.vertices.size() > 1){
			if(changed){
				visited.clear();
				vlist.clear();
				getPostOrder(g, 0, vlist, visited);
				currPos = 0;
				changed = false;
			}
			else if(currPos >= vlist.size() && !changed){
				break;
			}
			int v = vlist.get(currPos);
			ArrayList<Integer> toList = g.getToList(v);
			//test if it is a sequential path
			/*
			 * �ſ�ʼ��û��˳��ṹ�ģ���Ϊ�������ԭ��������˳��ṹ�������ڹ�Լ�Ĺ����н������˳��ṹ��ֻ�����м���ɵ�����
			 */
			if(toList.size() == 1){
				ArrayList<Integer> fromList = g.getFromList(toList.get(0));
				if(fromList.size() == 1){
					ArrayList<Integer> toList2 = g.getToList(toList.get(0));
					for(int node: toList2){
						g.addEdge(v, node);
						g.removeEdge(toList.get(0), node);
					}
					g.removeEdge(v, toList.get(0));
					g.removeVertex(toList.get(0));
					changed = true;
					// =================
					nodeinfo node1 = new nodeinfo();
					node1.type = "seq";
					node1.childlist.add(v);
					node1.childlist.add(toList.get(0));
					treeList.add(node1);
					// =================
				}
			}
			//test if it is an "if"
			else if(toList.size() == 2){
				//if-else ?
				ArrayList<Integer> fromList0 = g.getFromList(toList.get(0));
				ArrayList<Integer> fromList1 = g.getFromList(toList.get(1));
				ArrayList<Integer> toList0 = g.getToList(toList.get(0));
				ArrayList<Integer> toList1 = g.getToList(toList.get(1));
				
				if(fromList0.size() == 1 && fromList1.size() == 1){
					if(fromList0.get(0) == fromList1.get(0)){
						if(toList0.size() == 1 && toList1.size() == 1){
							if(toList0.get(0) == toList1.get(1)){
								g.removeEdge(v, toList.get(0));
								g.removeEdge(v, toList.get(1));
								g.removeEdge(toList.get(0), toList0.get(0));
								g.removeEdge(toList.get(1), toList1.get(0));
								g.addEdge(v, toList0.get(0));
								g.removeVertex(toList.get(0));
								g.removeVertex(toList.get(1));
								changed = true;
								
								// ==================
								nodeinfo node = new nodeinfo();
								node.type = "if-else";
								node.childlist.add(v);
								node.childlist.add(toList.get(0));
								node.childlist.add(toList.get(1));
								treeList.add(node);

//								nodeinfo node1 = new nodeinfo();
//								node1.type = "seq";
//								node1.childlist.add(v);
//								node1.childlist.add(toList0.get(0));
//								treeList.add(node1);
								// ==================

							}
						}
					}
				}else if(fromList0.size() == 1 && fromList1.size() >= 2){
					if(toList0.size() == 1 && toList0.get(0) == toList.get(1)){
						g.removeEdge(v, toList.get(0));
						g.removeEdge(toList.get(0), toList0.get(0));
						g.removeVertex(toList.get(0));
						changed = true;
						// ==============
						nodeinfo node = new nodeinfo();
						node.type = "if";
						node.childlist.add(v);
						node.childlist.add(toList.get(0));
						node.childlist.add(toList.get(1));
						treeList.add(node);
						// ==============
						
					}
				}else if(fromList1.size() == 1 && fromList0.size() >= 2){
					if(toList1.size() == 1 && toList1.get(0) == toList.get(0)){
						g.removeEdge(v, toList.get(1));
						g.removeEdge(toList.get(1), toList1.get(0));
						g.removeVertex(toList.get(1));
						changed = true;
						// ==============
						nodeinfo node = new nodeinfo();
						node.type = "if";
						node.childlist.add(v);
						node.childlist.add(toList.get(1));
						node.childlist.add(toList.get(0));
						treeList.add(node);
						// ==============
					}
				}
			}
			ArrayList<Integer> fromList = g.getFromList(v);
			//test if it is a "loop"
			if(fromList.size() == 1){
				if(g.hasEdge(fromList.get(0), v) && g.hasEdge(v, fromList.get(0))){
					if(g.outDegree(fromList.get(0)) == 2 && g.outDegree(v) == 1){
						g.removeEdge(v, fromList.get(0));
						g.removeEdge(fromList.get(0), v);
						g.removeVertex(v);
						changed = true;
						// ==============
						nodeinfo node = new nodeinfo();
						node.type = "while";
						node.childlist.add(fromList.get(0));
						node.childlist.add(v);
						treeList.add(node);
						// ==============
					}
					else if(g.outDegree(v) ==  2 && g.outDegree(fromList.get(0)) == 1){
						g.removeEdge(v, fromList.get(0));
						changed = true;
						// ==============
						nodeinfo node = new nodeinfo();
						node.type = "while";
						node.childlist.add(v);
						node.childlist.add(fromList.get(0));
						treeList.add(node);
						// ==============
					}
				}
			}
			currPos += 1;
		}
		System.out.println("/******�߼�������ƽṹ*******/");
		System.out.println("/*************************/");
		//�����Ϊ0�Ļ����鿪ʼ��ȡ�ṹ��
		codeGen1(0, 0);
	}

	/**
	 * �߼�����ṹ������
	 * @param index ���������
	 * @param step ��������
	 * @return �ṹ���Ƿ����
	 */
	public boolean codeGen1(int index, int step) {
		nodeinfo root = getBranchNode(index);
		if (root == null) {
			return true;
		} else {
			switch (root.type) {
				case "seq": {
					// ����Ҷ�ӽڵ�ʱ
					if (codeGen1(root.childlist.get(0), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(0));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(0) + ";");
					}
					if (codeGen1(root.childlist.get(1), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(1));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(1) + ";");
					}
					break;
				}
				case "if-else": {
					if (codeGen1(root.childlist.get(0), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(0));
						funcBodyStr.append("if(" + highCodeList.get(0).highstring + "){\n");
						System.out.println("if(" + root.childlist.get(0) + "){");
					}
					if (codeGen1(root.childlist.get(1), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(1));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(1) + ";");
					}
					funcBodyStr.append("}else{\n");
					System.out.println("}else{");
					if (codeGen1(root.childlist.get(2), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(2));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(2) + ";");
					}
					funcBodyStr.append("}\n");
					System.out.println("}");
					break;
				}
				case "if": {
					if (codeGen1(root.childlist.get(0), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(0));
						funcBodyStr.append("if(" + highCodeList.get(0).highstring + "){\n");
						System.out.println("if(" + root.childlist.get(0) + "){");
					}
					if (codeGen1(root.childlist.get(1), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(1));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(1) + ";");
					}
					funcBodyStr.append("}\n");
					System.out.println("}");
					break;
				}
				case "while": {
					if (codeGen1(root.childlist.get(0), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(0));
						funcBodyStr.append("while(" + highCodeList.get(0).highstring + "){\n");
						System.out.println("while(" + root.childlist.get(0) + "){");
					}
					if (codeGen1(root.childlist.get(1), ++step) == true) {
						ArrayList<highcode> highCodeList = block2code(root.childlist.get(1));
						int listsize = highCodeList.size();
						for (int i = 0; i < listsize; i++) {
							funcBodyStr.append(highCodeList.get(i).highstring + ";\n");
						}
						System.out.println(root.childlist.get(1) + ";");
					}
					funcBodyStr.append("}\n");
					System.out.println("}");
					break;
				}
			}
			return false;
		}
	}

	/**
	 * ���ջ��������ת���ɸ߼�����
	 * @param blockIndex ����������
	 * @return ������ĸ߼���������
	 */
	public ArrayList<highcode> block2code(int blockIndex) {
		ArrayList<highcode> highCodeList = new ArrayList<highcode>();
		ArrayList<AsmInstModel> Inslist =  blockList.get(blockIndex).getInstList();
		for (int i = 0; i < Inslist.size(); i++) {
			AsmInstModel inst = Inslist.get(i);
			String opStr = inst.getOp();
			switch (opStr) {
			//�Ƚ�
			case "cmp": {
				AsmInstModel nextInst = Inslist.get(i + 1);
				String nextOpStr = nextInst.getOp();
				ArrayList<String> argsList = inst.getArgList();
				highcode high = new highcode();
				high.hightype = opStr;
				String arg0 = argsList.get(0);
				String arg1 = argsList.get(1);
				if (nextOpStr.startsWith("bne")) {
					high.highstring = arg0 + "!=" + arg1;
				} else if (nextOpStr.startsWith("blt")) {
					high.highstring = arg0 + "<" + arg1;
				} else if (nextOpStr.startsWith("ble")) {
					high.highstring = arg0 + "<=" + arg1;
				} else if (nextOpStr.startsWith("bgt")) {
					high.highstring = arg0 + ">" + arg1;
				} else if (nextOpStr.startsWith("bge")) {
					high.highstring = arg0 + ">=" + arg1;
				}
				highCodeList.add(high);
				break;
			}
			//�ӷ�
			case "adds": {
				ArrayList<String> argsList = inst.getArgList();
				highcode high = new highcode();
				high.hightype = opStr;
				String arg0 = argsList.get(0);
				String arg1 = argsList.get(1);
				if (argsList.size() == 3) {
					String arg2 = argsList.get(2);
					high.highstring = arg0 + "=" + arg1 + "+" + arg2;
				} else {
					high.highstring = arg0 + "=" + arg0 + "+" + arg1;
				}
				highCodeList.add(high);
				break;
			}
			//�˷�*2
			case "lsls": {
				ArrayList<String> argsList = inst.getArgList();
				highcode high = new highcode();
				high.hightype = opStr;
				String arg0 = argsList.get(0);
				high.highstring = arg0 + "=" + arg0 + "*" + 2;
				highCodeList.add(high);
				break;
			}
			//���ݺ��������Ƿ��з��������ж��Ƿ���return
			case "bx": {
				ArrayList<String> argsList = inst.getArgList();
				String arg0 = argsList.get(0);
				highcode high = new highcode();
				high.hightype = opStr;
				//��ת�����ص�ַ
				if (arg0.equals("lr")) {
					if (funcHeadStr.contains("void")) {
						high.highstring = "return";
					}
					else {
						high.highstring = "return "+ "r0";
					}
				}
			}
			default:
				break;
			}
			
			
		}
		return highCodeList;
	}
	
	class VarNode implements Cloneable {
		public String varName;
		public ArrayList<String> regsSet;
		public ArrayList<String> typeSet;

		public VarNode() {
			regsSet = new ArrayList<String>();
			typeSet = new ArrayList<String>();
		}

		@Override
		public Object clone() {
			Object o = null;
			try {
				o = super.clone();
			} catch (CloneNotSupportedException e) {
				System.out.println(e.toString());
			}
			return o;
		}
	}
	
	/**
	 * ������
	 * @author zxs
	 *
	 */
	class Var{
		public String varName;
		public HashSet<Var> regsSet;
		public HashSet<String> typeSet;
		public Var() {
			regsSet = new HashSet<Var>();
			typeSet = new HashSet<String>();
		}
		public Var(String varName) {
			this.varName = varName;
			regsSet = new HashSet<Var>();
			typeSet = new HashSet<String>();
		}
		public void addVarReg(Var var){
			regsSet.add(var);
		}
		public void addVarType(String type){
			typeSet.add(type);
		}
		public int hashCode(){
			return varName.hashCode();
		}
		public boolean equals(Object obj){
			if(obj instanceof Var){
				Var p = (Var)obj;
				return(varName.equals(p.varName));
			}
			return super.equals(obj);
		}
	}
	
	public static final String[] binOps = {"mov","cmp"};//��Ԫ���������
	public static final String[] triOps = {"add","sub"};//��Ԫ���������
	/**
	 * ���ݺ��������б���б������͵����ʹ��ݷ���
	 * @param argsType �����Ĳ����б�(������Ϊ�Ĵ�����)
	 * BUG:����ֻ���Ǽ����͵Ĵ��ݷ��������������ϲ������͵ķ���
	 * ����������ڣ�
	 * 1���Ӻ����Ĳ����б�
	 * 		a/�����б���ĿС��4��r0~r3
	 * 		b/�����б���Ŀ����4������Ĳ��ִ��ջ��
	 * 2���ֲ�����
	 * 3������
	 * 
	 */
	public HashSet<Var> propagationType(ArrayList<String> argsType,AsmFuncModel funcModel){
		if (funcModel == null) {
			return null;
		}
		//��ʼ��V����
		HashSet<Var> V = new HashSet<>();
		for (int i = 0; i < argsType.size(); i++) {
			Var var = new Var();
			//ֻ����Java_�Ҳ�������С�ڵ���2�������������Ĭ�ϵĲ���JNIEnv* env,jobject thiz�����r0��r1�У�
			var.varName = "r"+(i+2);
			var.addVarType(argsType.get(i));
			V.add(var);
		}
		//�����ռ�
		ArrayList<AsmInstModel> instList = funcModel.getInstList();
		for (int i = 0; i < instList.size(); i++) {
			AsmInstModel instModel = instList.get(i);
			ArrayList<String> argsList = instModel.getArgList();
			/* ??????????????????????????????
			 * add(adds)���Ƕ�Ԫ�����������Ԫ�����
			 */
			//��Ԫ����������
			if (StringTools.isInStrings(binOps, instModel.getOp())) {
				int consIndex = argsList.get(0).startsWith("#") ? 0 : (argsList.get(1).startsWith("#") ? 1 : -1);
				//���г��������
				if (consIndex != -1) {
					Var var1 = new Var();
					var1.varName = argsList.get(1-consIndex);
					var1.addVarType("int");
					V.add(var1);
				}
				//�������������
				else {
					Var var1 = new Var(argsList.get(0));
					Var var2 = new Var(argsList.get(1));
					var1.addVarReg(var2);
					var2.addVarReg(var1);
					V.add(var1);
					V.add(var2);
				}
			}
			//��Ԫ����������
			else if (StringTools.isInStrings(triOps, instModel.getOp())) {
				int consIndex = argsList.get(0).startsWith("#") ? 0 : (argsList.get(1).startsWith("#") ? 1 : 
					(argsList.get(2).startsWith("#") ? 2 : -1));
				//���г��������
				if (consIndex != -1) {
					ArrayList<Var> tmpVarList = new ArrayList<>();
					for (int j = 0; j < 3; j++) {
						if (j != consIndex) {
							Var var = new Var(argsList.get(j));
							var.addVarType("int");
							tmpVarList.add(var);
						}
					}
					for (int j = 0; j < tmpVarList.size(); j++) {
						Var var = tmpVarList.get(j);
						for (int k = 0; k < tmpVarList.size(); k++) {
							if (j != k) {
								Var tmpVar = tmpVarList.get(k);
								var.addVarReg(tmpVar);
							}
						}
					}
					for (int j = 0; j < tmpVarList.size(); j++) {
						Var var = tmpVarList.get(j);
						V.add(var);
					}
				}
				else {
					Var var1 = new Var(argsList.get(0));
					Var var2 = new Var(argsList.get(1));
					Var var3 = new Var(argsList.get(2));
					var1.addVarReg(var2);var1.addVarReg(var3);
					var2.addVarReg(var1);var2.addVarReg(var3);
					var3.addVarReg(var1);var3.addVarReg(var2);
					V.add(var1);
					V.add(var2);
					V.add(var3);
				}
			}
			//�������.....
			else {
				
			}
		}
		//�������ʹ���
		boolean flag = false;
		while ( !flag ) {
			flag = true;
			for ( Var v : V) {
				for ( Var w : v.regsSet) {
					/* ??????????????????????????????
					 * T�����а�����ͬ����
					 */
					if (!StringTools.isTequal(v.typeSet, w.typeSet)) {
						flag = false;
						v.typeSet.addAll(w.typeSet);
						w.typeSet.addAll(v.typeSet);
					}
				}
			}
		}
		return V;
	}

	/*
	 * �������ͷ���
	 */
	public void typeAnalysis1(String argString) {
		
		//��ʼ��
		ArrayList<Node> L = new ArrayList<Node>();

		ArrayList<AsmInstModel> instList = func.getInstList();
		int instListSize = instList.size();
		for (int i = 0; i < instListSize; i++) {
			Node node = new Node();
			AsmInstModel inst = instList.get(i);
			node.op = inst.getOp();
			node.argsList = inst.getArgList();
			L.add(node);
		}

		V1 = new ArrayList<VarNode>();
		P = new ArrayList<VarNode>();
		String temp[] = argString.split(" ");
		for (int i = 0; i < temp.length; i++) {
			VarNode var = new VarNode();
			var.typeSet.add(temp[i++]);
			var.varName = temp[i];
			P.add(var);
		}
		// ============�����ռ�=============
		V1 = P;
		int index;
		int Lsize = L.size();
		for (int i = 0; i < Lsize; i++) {
			Node I = L.get(i);
			if (I.op.startsWith("mov") || I.op.startsWith("cmp")) {
				String vname = I.argsList.get(0);
				String wname = I.argsList.get(1);
				ArrayList<String> wset = new ArrayList<String>();
				wset.add(wname);
				ArrayList<String> vset = new ArrayList<String>();
				vset.add(vname);
				int mark0 = -1, mark1 = -1;
				index = SearchVar(vname);
				VarNode vVar = new VarNode();
				if (index > -1) {
					vVar = V1.get(index);
					mark0 = index;
				} else {
					vVar.varName = vname;
				}
				index = SearchVar(wname);
				VarNode wVar = new VarNode();
				if (index > -1) {
					wVar = V1.get(index);
					mark1 = index;
				} else {
					wVar.varName = wname;
				}
				if (mark0 == -1 && mark1 == -1) {
					vVar.regsSet = union(vVar.regsSet, wset);
					wVar.regsSet = union(wVar.regsSet, vset);
					V1.add(vVar);
					V1.add(wVar);
				} else if (mark0 == -1 && mark1 != -1) {
					vVar.regsSet = union(vVar.regsSet, wset);
					V1.get(mark1).regsSet = union(V1.get(mark1).regsSet, vset);
					V1.add(vVar);
				} else if (mark0 != -1 && mark1 == -1) {
					V1.get(mark0).regsSet = union(V1.get(mark0).regsSet, wset);
					wVar.regsSet = union(wVar.regsSet, vset);
					V1.add(wVar);
				} else if (mark0 != -1 && mark1 != -1) {
					V1.get(mark0).regsSet = union(V1.get(mark0).regsSet, wset);
					V1.get(mark1).regsSet = union(V1.get(mark1).regsSet, vset);
				}
			} else if (I.op.startsWith("add")) {
				String src1name = "";
				String src2name = "";
				String destname = "";
				if (I.argsList.size() == 2) {
					src1name = I.argsList.get(0);
					src2name = I.argsList.get(1);
					destname = "1";
				} else if (I.argsList.size() == 3) {
					src1name = I.argsList.get(0);
					src2name = I.argsList.get(1);
					destname = I.argsList.get(2);
				}
				ArrayList<String> src1set = new ArrayList<String>();
				src1set.add(src1name);
				ArrayList<String> src2set = new ArrayList<String>();
				src2set.add(src2name);
				ArrayList<String> destset = new ArrayList<String>();
				destset.add(destname);

				int flag0 = -1, flag1 = -1, flag2 = -1;

				index = SearchVar(src1name);
				VarNode src1Var = new VarNode();
				if (index > -1) {
					src1Var = V1.get(index);
					flag0 = index;
				} else {
					src1Var.varName = src1name;
				}

				index = SearchVar(src2name);
				VarNode src2Var = new VarNode();
				if (index > -1) {
					src2Var = V1.get(index);
					flag1 = index;
				} else {
					src2Var.varName = src2name;
				}

				index = SearchVar(destname);
				VarNode destVar = new VarNode();
				if (index > -1) {
					destVar = V1.get(index);
					flag2 = index;
				} else {
					destVar.varName = destname;
				}

				ArrayList<String> set1 = new ArrayList<String>();
				set1.add(src2name);
				set1.add(destname);
				ArrayList<String> set2 = new ArrayList<String>();
				set2.add(src1name);
				set2.add(destname);
				ArrayList<String> set3 = new ArrayList<String>();
				set3.add(src1name);
				set3.add(src2name);
				if (flag0 == -1 && flag1 == -1 && flag2 == -1) {
					src1Var.regsSet = union(src1Var.regsSet, set1);
					src2Var.regsSet = union(src2Var.regsSet, set2);
					destVar.regsSet = union(destVar.regsSet, set3);
					V1.add(src1Var);
					V1.add(src2Var);
					V1.add(destVar);
				} else if (flag0 == -1 && flag1 == -1 && flag2 != -1) {
					src1Var.regsSet = union(src1Var.regsSet, set1);
					src2Var.regsSet = union(src2Var.regsSet, set2);
					V1.get(flag2).regsSet = union(V1.get(flag2).regsSet, set3);
					V1.add(src1Var);
					V1.add(src2Var);
				} else if (flag0 == -1 && flag1 != -1 && flag2 == -1) {
					src1Var.regsSet = union(src1Var.regsSet, set1);
					V1.get(flag1).regsSet = union(V1.get(flag1).regsSet, set2);
					destVar.regsSet = union(destVar.regsSet, set3);
					V1.add(src1Var);
					V1.add(destVar);
				} else if (flag0 == -1 && flag1 != -1 && flag2 != -1) {
					src1Var.regsSet = union(src1Var.regsSet, set1);
					V1.get(flag1).regsSet = union(V1.get(flag1).regsSet, set2);
					V1.get(flag2).regsSet = union(V1.get(flag2).regsSet, set3);
					V1.add(src1Var);
				} else if (flag0 != -1 && flag1 == -1 && flag2 == -1) {
					V1.get(flag0).regsSet = union(V1.get(flag0).regsSet, set1);
					src2Var.regsSet = union(src2Var.regsSet, set2);
					destVar.regsSet = union(destVar.regsSet, set3);
					V1.add(src2Var);
					V1.add(destVar);
				} else if (flag0 != -1 && flag1 == -1 && flag2 != -1) {
					V1.get(flag0).regsSet = union(V1.get(flag0).regsSet, set1);
					src2Var.regsSet = union(src2Var.regsSet, set2);
					V1.get(flag2).regsSet = union(V1.get(flag2).regsSet, set3);
					V1.add(src2Var);
				} else if (flag0 != -1 && flag1 != -1 && flag2 == -1) {
					src1Var.regsSet = union(src1Var.regsSet, set1);
					src2Var.regsSet = union(src2Var.regsSet, set2);
					V1.get(flag2).regsSet = union(V1.get(flag2).regsSet, set3);
					V1.add(src1Var);
					V1.add(src2Var);
				} else if (flag0 != -1 && flag1 != -1 && flag2 != -1) {
					V1.get(flag0).regsSet = union(V1.get(flag0).regsSet, set1);
					V1.get(flag1).regsSet = union(V1.get(flag1).regsSet, set2);
					V1.get(flag2).regsSet = union(V1.get(flag2).regsSet, set3);
				}

			}
			// .....
		}
		// ============���ʹ���=============
		boolean flag = false;

		int lun = 0;

		while (!flag) {
			flag = true;
			lun++;
			System.out.println("==============" + "  ��" + lun + "��  "
					+ "===============");
			int Vsize = V1.size();
			for (int i = 0; i < Vsize; i++) {
				VarNode v = (VarNode) V1.get(i).clone();
				// =============================================
				String string = "";
				for (int ii = 0; ii < v.typeSet.size(); ii++) {
					string = string + "  " + v.typeSet.get(ii);
				}
				System.out.println(v.varName + ":" + string);
				// =============================================
				int vSsize = v.regsSet.size();
				for (int k = 0; k < vSsize; k++) {
					String w = v.regsSet.get(k);
					VarNode wNode;
					for (int j = 0; j < Vsize; j++) {
						VarNode temp1 = (VarNode) V1.get(j).clone();
						if (temp1.varName.equals(w)) {
							wNode = temp1;
							// =============================================
							String string1 = "";
							for (int ii = 0; ii < wNode.typeSet.size(); ii++) {
								string1 = string1 + "  " + wNode.typeSet.get(ii);
							}
							System.out.println("   +" + wNode.varName + ":"
									+ string1);
							// =============================================
							Collections.sort(v.typeSet);
							Collections.sort(wNode.typeSet);
							if (!v.typeSet.equals(wNode.typeSet)) {
								flag = false;
								V1.get(i).typeSet = union(V1.get(i).typeSet, wNode.typeSet);
								V1.get(j).typeSet = union(V1.get(j).typeSet, v.typeSet);
							}
							break;
						}
					}

				}
			}
		}
		System.out.println("a");
	}

	/*
	 * ��ӡ����
	 */
	public void printVars() {
		int V1_size = V1.size();
		for (int i = 0; i < V1_size; i++) {
			VarNode var = V1.get(i);
			if (var.varName.contains("r")) {
				myView.showContent(var.typeSet.get(0) + "  " + var.varName + ";\n\r", 1);
			}
		}
		myView.showContent("\n\r", 0);
	}

	/*
	 * �õ���������
	 */
	public String getType(String r) {
		int V1_size = V1.size();
		for (int i = 0; i < V1_size; i++) {
			if (V1.get(i).varName.equals(r)) {
				return V1.get(i).typeSet.get(0);
			}
		}
		return "";
	}

	/*
	 * ���������鶨ֵ���γ�
	 */
	public void EPGen(AsmFuncModel func) {

	}

	// ====================================
	// �������ͷ����õ��Ľṹ�ͷ���
	// ====================================
	class Node {
		String op;
		ArrayList<String> argsList;
	}

	

	public ArrayList<String> union(ArrayList<String> ls, ArrayList<String> ls2) {
		ArrayList<String> list = new ArrayList<String>();
		for (String string : ls) {
			if (list.contains(string))
				continue;
			else {
				list.add(string);
			}
		}
		for (String string : ls2) {
			if (list.contains(string))
				continue;
			else {
				list.add(string);
			}
		}
		return list;
	}

	public int SearchVar(String temp) {
		int Vsize = V1.size();
		for (int i = 0; i < Vsize; i++) {
			if (V1.get(i).varName.equals(temp)) {
				return i;
			}
		}
		return -1;
	}

	// ====================================
	// ���ɽṹ���Ĺ��ߺ���
	// ====================================
	class nodeinfo {
		String type = null;
		ArrayList<Integer> childlist;

		public nodeinfo() {
			childlist = new ArrayList<Integer>();
		}
	}

	public void addVertex(int v) {
		if (vertices.indexOf(v) < 0) {
			vertices.add(v);
			adjacentTable.add(new ArrayList<Integer>());
		}
	}

	public void addEdge(int f, int t) {
		int index = vertices.indexOf(f);
		if (index < 0) {
			System.out.println("addEdge: cant find the source vertex!");
			System.exit(1);
		}

		ArrayList<Integer> targets = adjacentTable.get(index);
		if (targets.indexOf(t) < 0) {
			targets.add(t);
		}
	}

	public void getPostOrder(AsmAdCode g, int root, ArrayList<Integer> vlist,
			ArrayList<Integer> visited) {
		visited.add(root);
		int index = vertices.indexOf(root);
		if (index >= 0) {
			ArrayList<Integer> adjacentList = adjacentTable.get(index);
			for (int i = 0; i < adjacentList.size(); i++) {
				if (visited.indexOf(adjacentList.get(i)) < 0) {
					getPostOrder(g, adjacentList.get(i), vlist, visited);
				}
			}
		}
		vlist.add(root);
	}

	public ArrayList<Integer> getToList(int v) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int index = vertices.indexOf(v);
		if (index >= 0) {
			list.addAll(adjacentTable.get(index));
		}
		return list;
	}

	public ArrayList<Integer> getFromList(int v) {
		ArrayList<Integer> froms = new ArrayList<Integer>();
		for (int i = 0; i < adjacentTable.size(); i++) {
			ArrayList<Integer> l = adjacentTable.get(i);
			int index = l.indexOf(v);
			if (index >= 0) {
				froms.add(vertices.get(i));
			}
		}
		return froms;
	}

	public void removeEdge(int from, int to) {
		int index = vertices.indexOf(from);
		if (index >= 0) {
			ArrayList<Integer> list = adjacentTable.get(index);
			index = list.indexOf(to);
			list.remove(index);
		}
	}

	public void removeVertex(int v) {
		int index = vertices.indexOf(v);
		if (index >= 0) {
			vertices.remove(index);
			adjacentTable.remove(index);
		}
	}

	public boolean hasEdge(int from, int to) {
		int index = vertices.indexOf(from);
		if (index >= 0) {
			if (adjacentTable.get(index).contains(to)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int outDegree(int v) {
		int index = vertices.indexOf(v);
		if (index >= 0) {
			return adjacentTable.get(index).size();
		}

		return 0;
	}

	public AsmAdCode makeACopy() {
		AsmAdCode c = new AsmAdCode(this);
		return c;
	}

	public AsmAdCode(AsmAdCode graph) {
		this.vertices = new ArrayList<Integer>();
		this.adjacentTable = new ArrayList<ArrayList<Integer>>();
		this.vertices.addAll(graph.getVertices());
		for (int i = 0; i < this.vertices.size(); i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.addAll(graph.getTable().get(i));
			this.adjacentTable.add(list);
		}
	}

	public AsmAdCode() {}

	public ArrayList<Integer> getVertices() {
		return this.vertices;
	}

	public ArrayList<ArrayList<Integer>> getTable() {
		return this.adjacentTable;
	}

	// ====================================
	// ������ת�߼������õ��Ľṹ�ͺ���
	// ====================================
	/**
	 * ���ջ�������Ż�ȡ��Լʱ���ӹ�Լ��ʼ�㣬�������treeList��ɾ��
	 * @param node ���������
	 * @return
	 */
	public nodeinfo getBranchNode(int node) {
		int NodeTreeNum = treeList.size();
		for (int i = NodeTreeNum - 1; i >= 0; i--) {
			if (treeList.get(i).childlist.get(0) == node) {
				nodeinfo node2 = treeList.get(i);
				treeList.remove(i);
				return node2;
			}
		}
		return null;
	}

	class highcode {
		public String highstring = "";
		public String hightype = "";
	}

}
