package core.dissambler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.eclipse.ui.console.MessageConsoleStream;

import core.dissambler.model.AsmFunc;
import core.dissambler.model.AsmInst;
import view.ConsoleFactory;
/**
 * 对单个函数进行基本块的划分；控制流的分析
 * @author zxs
 *
 */
public class AsmFuncAna{
	
	/**
	 * 生成控制流图
	 * @param
	 * 1、基本块k在流图中的位置紧跟在基本块i之后且i的出口语句不是无条件转移或停止语句
	 * 2、基本块i的出口语句是goto(s)或者if...goto(s)且(s)是基本块k的入口语句
	 * i为k的前驱，k为i的后继 
	 */
	public AsmFunc genCfg(AsmFunc funcModel){
		
		genBasicBlock(funcModel);
		
		ArrayList<AsmBlock> blockList = funcModel.getBlockList();
		int blockListSize = blockList.size();
		
		for(int i=0;i<blockListSize;i++){
			AsmBlock block = blockList.get(i);
			ArrayList<AsmInst> instList = block.getInstList();
			AsmInst lastInstModel = instList.get(instList.size()-1);
			String lastInstOp = lastInstModel.getOp();
			//基本块i的最后一条语句是跳转语句
			if(isJumpIns(lastInstModel)){
				long jumpedAddr = 0L;
				try {
					jumpedAddr = Integer.parseInt(lastInstModel.getArgList().get(0), 16);
				} catch (NumberFormatException e) {
					System.out.println(e.toString());
				}
				for(int k=0;k<blockListSize;k++){
					AsmBlock jumpedBlock = blockList.get(k);
					long jumpedBlockAddr = jumpedBlock.getInstList().get(0).getAddr();
					//2、基本块i的出口语句是goto(s)或者if...goto(s)且(s)是基本块k的入口语句
					if ( jumpedBlockAddr == jumpedAddr) {
						block.addSubBlockIndex(jumpedBlock);
						jumpedBlock.addPreBlockIndex(block);
						break;
					}
				}
			}
			//1、基本块k在流图中的位置紧跟在基本块i之后且i的出口语句不是无条件转移或停止语句
			//BUG:没有将POP当做停止语句
			if (!isNoConsJumpIns(lastInstModel)) {
				if (i+1 < blockListSize) {
					AsmBlock nextBlockModel = blockList.get(i+1);
					nextBlockModel.addPreBlockIndex(block);
					block.addSubBlockIndex(nextBlockModel);
				}
			}
		}
		return funcModel;
	}
	
	/**
	 * 根据函数名划分基本块
	 * @param funcName
	 * 1、由该入口语句直到下一个入口语句（不包含下一个入口语句）之间的所有语句构成一个基本块。
	 * 2、由该入口语句到一转移语句（含该转移语句）之间的所有语句构成一个基本块；或到程序中的
	 * 停止或暂停语句（包含该停止或暂停语句）之间的语句序列组成的。
	 */
	private void genBasicBlock(AsmFunc funcModel){
		int bNo = 0;
		AsmBlock blockModel = null;
		Stack<AsmInst> stack = new Stack<>();
		ArrayList<AsmBlock> blockList = new ArrayList<>();
		HashMap<Integer, AsmBlock> blockMap = new HashMap<>();
		//标记函数的基本块入口
		markBhead(funcModel);
		
		ArrayList<AsmInst> instList = funcModel.getInstList();
		int instListSize = instList.size();
		for (int i = 0; i < instListSize; i++) {
			AsmInst instModel = instList.get(i);
			//基本块的入口语句
			if (instModel.isHead()) {
				//清空blockModel
				if (blockModel != null) {
					blockList.add(blockModel);
					blockMap.put(blockModel.getbNo(), blockModel);
				}
				blockModel = new AsmBlock();
				stack.push(instModel);
				blockModel.setbNo(bNo++);
				blockModel.addInstModel(instModel);
				continue;
			}
//			//下一个入口语句
//			if (instModel.isHead() && !stack.isEmpty()) {
//				stack.pop();
//				blockModel.addInstModel(instModel);
//				//将block存入blocklist中
//				blockList.add(blockModel);
//				blockMap.put(blockModel.getbNo(), blockModel);
//				continue;
//			}
			//转移语句或停止暂停语句
			if (isJumpIns(instModel)) {
				/////
				stack.pop();
				blockModel.addInstModel(instModel);
				//将block存入blocklist中
				blockList.add(blockModel);
				blockMap.put(blockModel.getbNo(), blockModel);
				blockModel = null;
				continue;
			}
			if (blockModel != null) {
				blockModel.addInstModel(instModel);
			}
		}
		if (blockModel != null) {
			blockList.add(blockModel);
			blockMap.put(blockModel.getbNo(), blockModel);
		}
		funcModel.setBlockList(blockList);
		funcModel.setBlockMap(blockMap);
	}
	
	/**
	 * 标记基本块的入口地址
	 * @param curFunc
	 */
	private void markBhead(AsmFunc funcModel){
		
		ArrayList<AsmInst> instList = funcModel.getInstList();
		long instListSize = instList.size();
		//(1)程序的第一个语句
		AsmInst firstInstModel = instList.get(0);
		firstInstModel.setHead();
		for(int i=0;i<instListSize;i++){
			AsmInst instModel = instList.get(i);
			//当是跳转语句时
			if(isJumpIns(instModel)){
				//(2)紧跟在条件转移语句后面的语句 
				if(isConsJumpIns(instModel) && i<(instListSize-1)){
					AsmInst nextInstModel = instList.get(i+1);
					nextInstModel.setHead();
				}
				ArrayList<String> args = instModel.getArgList();
				//(3)条件转移语句或无条件转移语句的转移目标语句
				//BUG:跳转寄存器没有考虑
				//System.out.println(args.toString());
				if (args.size() > 0 && !args.get(0).contains("r")) {
					long addr = Integer.parseInt(args.get(0), 16);
					HashMap<Long, AsmInst> instMap = funcModel.getInstMap();
					AsmInst jumpedInstModel = instMap.get(addr);
					if (jumpedInstModel != null) {
						jumpedInstModel.setHead();
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
