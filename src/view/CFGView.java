package view;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.series.Force;
import com.github.abel533.echarts.series.force.Link;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.style.LinkStyle;
import com.github.abel533.echarts.style.NodeStyle;
import com.github.abel533.echarts.style.TextStyle;
import com.github.abel533.echarts.util.EnhancedOption;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import utils.Global;
import core.disassembler.model.AsmBlock;
import core.disassembler.model.AsmFunc;


public class CFGView extends ViewPart {

	Browser browser=null;
	public CFGView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		//composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		//composite.setLayout(new GridLayout());
		//frame = (JFrame) SWT_AWT.new_Frame(parent);
		browser = new Browser(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		browser.addTitleListener(new TitleListener() {
			  @Override
		  	  public void changed(TitleEvent event) {
				// TODO Auto-generated method stub
				
			 }
		   });
		//browser.setUrl(Constant.OUTPUTFOLDER+"force1.html");
		browser.setBounds(parent.getBounds());
		browser.setVisible(true);
		
		parent.addControlListener(new ControlAdapter() {
	        @Override
	        public void controlResized(final ControlEvent e) {
	            //System.out.println("RESIZE");
	            //browser.setBounds(parent.getBounds());
	        	//browser.setUrl(Constant.OUTPUTFOLDER+"CFG.html");
	        	browser.redraw();
	        }
	    });
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void drawControlFlowGraph(AsmFunc funcModel) {
		// use ECharts to draw charts
		
		EnhancedOption option = new EnhancedOption();
	    option.title().text(funcModel.getFuncName()).subtext("函数控制流图").x(X.right).y(Y.bottom);
	    option.tooltip().trigger(Trigger.item).formatter("{a} : {b}");
	    option.toolbox().show(true).feature(Tool.restore, Tool.saveAsImage);
	    //option.legend("家人", "朋友").legend().x(X.left);
	
	    //数据
	    Force force = new Force("调用关系");
	    force.categories("Block", "this Function", "other Function");
	    force.itemStyle().normal()
	            .label(new Label().show(true).textStyle(new TextStyle().color("#333")))
	            .nodeStyle().brushType(BrushType.both).color("rgba(255,215,200,0.4)").borderWidth(1);
	
	    force.itemStyle().emphasis()
	            .linkStyle(new LinkStyle())
	            .nodeStyle(new NodeStyle())
	            .label().show(true);
	    force.useWorker(false).minRadius(15).maxRadius(25).gravity(1.1).scaling(1.1).linkSymbol(Symbol.arrow);
	    
	    
	    String funcName = funcModel.getFuncName();
	    ///////////////////////////////////////////
	    System.out.println("\n//////////////////////////////////////////"+funcName);
	    HashMap<String, Node> nodeMap = new HashMap<String, Node>();
		for (AsmBlock block: funcModel.getBlockList()) {
			///////////////////////////////////////////
			System.out.println(block.toString());
			
			if(!nodeMap.containsKey(block.toString()))    
				nodeMap.put(block.toString(), new Node(1, block.toString(), block.getbNoStr(), block.getbNo()==0?15:10));
			for (AsmBlock sublock : block.getSubBlockSet()){
				//////////////////////////////////////////////
				System.out.println("--------"+sublock.toString());
				
				if(!nodeMap.containsKey(sublock.toString())){
					//if the sublock belongs to other function,then show the function name
					if(sublock.getFuncName()== funcName)
						nodeMap.put(sublock.toString(), new Node(1, sublock.toString() ,sublock.getbNoStr(), 10));
					else
						nodeMap.put(sublock.toString(), new Node(2, sublock.toString() ,sublock.toString(), 10));
				}
				force.links(new Link(block.toString(), sublock.toString(), 10));
			}
		}
		
		force.nodes(new ArrayList(nodeMap.values()));
		option.series(force);
	    option.exportToHtml(Global.OUTPUTFOLDER+"CFG.html");
	    browser.setUrl(Global.OUTPUTFOLDER+"CFG.html");
 	}

	/*
	 * 
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
