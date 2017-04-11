package view;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import dissambler.AsmBlockModel;
import dissambler.AsmFuncModel;
import dissambler.AsmInstModel;
import dissambler.AsmStructAna;
import dissambler.AsmTextSectionStruct;

public class CFGView extends ViewPart {

	private Composite composite;
	private mxCell node1;
	private mxCell node2;
	private int width = 150;
	private int height = 40;
	private String shap = null;
	private ArrayList<mxCell> NodeList;
	private ArrayList<String> NodeListText;
	protected static mxGraphComponent graghComponent = new mxGraphComponent(
			new mxGraph());
	private Frame frame;
	AsmFuncModel funcModel;
	
	public CFGView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void drawCFG(AsmFuncModel funcModel) {
		frame = SWT_AWT.new_Frame(composite);
		
		NodeListText = new ArrayList<String>();
		NodeList = new ArrayList<mxCell>();

		final mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();

		graghComponent.setConnectable(false);
		graph.setAllowDanglingEdges(false);
		graph.setCloneInvalidEdges(false);
		graph.setCellsEditable(false);
		graph.setCellsResizable(false);
		graph.setAutoSizeCells(true);
		
		
		HashMap<Integer, mxCell> hashMap = new HashMap<>();
		ArrayList<AsmBlockModel> blockList = funcModel.getBlockList();
		int blockListSize = blockList.size();
		try {
			for (int i = 0; i < blockListSize; i++) {
				AsmBlockModel block = blockList.get(i);
				if (hashMap.get(block.getbNo()) == null) {
					node1 = (mxCell) graph.insertVertex(parent, null, block.getbNo(), 20,
							20, width, height, shap);
					graph.updateCellSize(node1);
					hashMap.put(block.getbNo(), node1);
				}
				else {
					node1 = hashMap.get(block.getbNo());
				}
				ArrayList<AsmBlockModel> subBlockSet = block.getSubBlockSet();
				for (int j = 0; j < subBlockSet.size(); j++) {
					AsmBlockModel sublock = subBlockSet.get(j);
					if (hashMap.get(sublock.getbNo()) == null) {
						node2 = (mxCell) graph.insertVertex(parent, null, sublock.getbNo(), 20,
								20, width, height, shap);
						graph.updateCellSize(node2);
						hashMap.put(sublock.getbNo(), node2);
					}
					else {
						node2 = hashMap.get(sublock.getbNo());
					}
					graph.insertEdge(parent, null, null, node1, node2);
				}
			}
			mxIGraphLayout layout = new mxHierarchicalLayout(graph);
			layout = new mxHierarchicalLayout(graph, SwingConstants.NORTH);
			layout.execute(graph.getDefaultParent());
			// layout = new mxCompactTreeLayout(graph, false);
			// layout = new mxCompactTreeLayout(graph, true);
			// layout = new mxParallelEdgeLayout(graph);
			// layout = new mxEdgeLabelLayout(graph);
			// layout = new mxOrganicLayout(graph);
		}
		finally {
			graph.getModel().endUpdate();
		}
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		SwingUtilities.invokeLater(new Runnable() {
			 @Override
			public void run() {
				 frame.add(graphComponent);
			 }
		});
		/*
		 * 点击控制流图结点
		 */
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			@Override
			public void mouseReleased(MouseEvent e)
			{
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				
				if (cell != null)
				{
					String blocknum = graph.getLabel(cell);
					//System.out.println(blocknum);
					showBlock(blocknum);
				}
			}
		});
	}

	/*
	 * 通过基本块号高亮汇编代码
	 */
	public void showBlock(String blocknum){
//		int num =Integer.parseInt(blocknum);
//		ArrayList<AsmInstModel> instlist = func.getInstList();
//		AsmBlockModel block = func.getBlockList().get(num);
//		int start = block.getStart();
//		int end = block.getEnd();
//		for(int i=start;i<=end;i++){
//			System.out.println(instlist.get(i).getInstLine());
//		}
	}
}
