package dissambler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
/**
 * �Ե����������л�����Ļ��֣��������ķ���
 * @author zxs
 *
 */
public class AsmStructAna{
	
	/**
	 * ���ɿ�����ͼ
	 * @param
	 * 1��������k����ͼ�е�λ�ý����ڻ�����i֮����i�ĳ�����䲻��������ת�ƻ�ֹͣ���
	 * 2��������i�ĳ��������goto(s)����if...goto(s)��(s)�ǻ�����k��������
	 * iΪk��ǰ����kΪi�ĺ�� 
	 */
	public AsmFuncModel genCfg(AsmFuncModel funcModel){
		
		genBasicBlock(funcModel);
		
		ArrayList<AsmBlockModel> blockList = funcModel.getBlockList();
		int blockListSize = blockList.size();
		
		for(int i=0;i<blockListSize;i++){
			AsmBlockModel block = blockList.get(i);
			ArrayList<AsmInstModel> instList = block.getInstList();
			AsmInstModel lastInstModel = instList.get(instList.size()-1);
			String lastInstOp = lastInstModel.getOp();
			//������i�����һ���������ת���
			if(isJumpIns(lastInstModel)){
				long jumpedAddr = 0L;
				try {
					jumpedAddr = Integer.parseInt(lastInstModel.getArgList().get(0), 16);
				} catch (NumberFormatException e) {
					System.out.println(e.toString());
				}
				for(int k=0;k<blockListSize;k++){
					AsmBlockModel jumpedBlock = blockList.get(k);
					long jumpedBlockAddr = jumpedBlock.getInstList().get(0).getAddr();
					//2��������i�ĳ��������goto(s)����if...goto(s)��(s)�ǻ�����k��������
					if ( jumpedBlockAddr == jumpedAddr) {
						block.addSubBlockIndex(jumpedBlock);
						jumpedBlock.addPreBlockIndex(block);
						break;
					}
				}
			}
			//1��������k����ͼ�е�λ�ý����ڻ�����i֮����i�ĳ�����䲻��������ת�ƻ�ֹͣ���
			//BUG:û�н�POP����ֹͣ���
			if (!isNoConsJumpIns(lastInstModel)) {
				if (i+1 < blockListSize) {
					AsmBlockModel nextBlockModel = blockList.get(i+1);
					nextBlockModel.addPreBlockIndex(block);
					block.addSubBlockIndex(nextBlockModel);
				}
			}
		}
		return funcModel;
	}
	
	/**
	 * ���ݺ��������ֻ�����
	 * @param funcName
	 * 1���ɸ�������ֱ����һ�������䣨��������һ�������䣩֮���������乹��һ�������顣
	 * 2���ɸ������䵽һת����䣨����ת����䣩֮���������乹��һ�������飻�򵽳����е�
	 * ֹͣ����ͣ��䣨������ֹͣ����ͣ��䣩֮������������ɵġ�
	 */
	private void genBasicBlock(AsmFuncModel funcModel){
		int bNo = 0;
		AsmBlockModel blockModel = null;
		Stack<AsmInstModel> stack = new Stack<>();
		ArrayList<AsmBlockModel> blockList = new ArrayList<>();
		HashMap<Integer, AsmBlockModel> blockMap = new HashMap<>();
		//��Ǻ����Ļ��������
		markBhead(funcModel);
		
		ArrayList<AsmInstModel> instList = funcModel.getInstList();
		int instListSize = instList.size();
		for (int i = 0; i < instListSize; i++) {
			AsmInstModel instModel = instList.get(i);
			//�������������
			if (instModel.isHead()) {
				//���blockModel
				if (blockModel != null) {
					blockList.add(blockModel);
					blockMap.put(blockModel.getbNo(), blockModel);
				}
				blockModel = new AsmBlockModel();
				stack.push(instModel);
				blockModel.setbNo(bNo++);
				blockModel.addInstModel(instModel);
				continue;
			}
//			//��һ��������
//			if (instModel.isHead() && !stack.isEmpty()) {
//				stack.pop();
//				blockModel.addInstModel(instModel);
//				//��block����blocklist��
//				blockList.add(blockModel);
//				blockMap.put(blockModel.getbNo(), blockModel);
//				continue;
//			}
			//ת������ֹͣ��ͣ���
			if (isJumpIns(instModel)) {
				stack.pop();
				blockModel.addInstModel(instModel);
				//��block����blocklist��
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
	 * ��ǻ��������ڵ�ַ
	 * @param curFunc
	 */
	private void markBhead(AsmFuncModel funcModel){
		
		ArrayList<AsmInstModel> instList = funcModel.getInstList();
		long instListSize = instList.size();
		//(1)����ĵ�һ�����
		AsmInstModel firstInstModel = instList.get(0);
		firstInstModel.setHead();
		for(int i=0;i<instListSize;i++){
			AsmInstModel instModel = instList.get(i);
			//������ת���ʱ
			if(isJumpIns(instModel)){
				//(2)����������ת������������ 
				if(isConsJumpIns(instModel) && i<(instListSize-1)){
					AsmInstModel nextInstModel = instList.get(i+1);
					nextInstModel.setHead();
				}
				ArrayList<String> args = instModel.getArgList();
				//(3)����ת������������ת������ת��Ŀ�����
				//BUG:��ת�Ĵ���û�п���
				//System.out.println(args.toString());
				if (args.size() > 0 && !args.get(0).contains("r")) {
					long addr = Integer.parseInt(args.get(0), 16);
					HashMap<Long, AsmInstModel> instMap = funcModel.getInstMap();
					AsmInstModel jumpedInstModel = instMap.get(addr);
					if (jumpedInstModel != null) {
						jumpedInstModel.setHead();
					}
				}
			}
		}
	}
	/**
	 * �жϸ�ָ���ǲ���ת�����
	 * @param instModel
	 * @return
	 */
	private boolean isJumpIns(AsmInstModel instModel){
		String op = instModel.getOp();
		if (op.startsWith("blx") || op.startsWith("bl") ||
			op.startsWith("bx") || op.startsWith("b")) {
			return true;
		}
		return false;
	}
	/**
	 * �ж��ǲ���������ת��ָ��
	 * @param instModel
	 * @return
	 */
	private boolean isNoConsJumpIns(AsmInstModel instModel){
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
	 * �ж��ǲ�������ת��ָ��
	 * @param instModel
	 * @return
	 */
	private boolean isConsJumpIns(AsmInstModel instModel){
		if (isJumpIns(instModel) && !isNoConsJumpIns(instModel)) {
			return true;
		}
		return false;
	}
  
	
	
}
