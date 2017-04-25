package view;

import java.util.ArrayList;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import core.dissambler.AsmBlockModel;
import core.dissambler.AsmFuncModel;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.code.BrushType;
import com.github.abel533.echarts.code.Symbol;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
//import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.series.Force;
import com.github.abel533.echarts.series.force.Link;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.style.LinkStyle;
import com.github.abel533.echarts.style.NodeStyle;
import com.github.abel533.echarts.style.TextStyle;
import com.github.abel533.echarts.util.EnhancedOption;

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
		//browser.setUrl("file://d:/tmp/force1.html");
		browser.setBounds(parent.getBounds());
		browser.setVisible(true);
		
		parent.addControlListener(new ControlAdapter() {
	        @Override
	        public void controlResized(final ControlEvent e) {
	            //System.out.println("RESIZE");
	            //browser.setBounds(parent.getBounds());
	        	//browser.setUrl("file://d:/tmp/force1.html");
	        	browser.redraw();
	        }
	    });
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void drawCFG(AsmFuncModel funcModel) {
		// use ECharts to draw charts
		EnhancedOption option = new EnhancedOption();
	    option.title().text("人物关系：乔布斯").subtext("数据来自人立方").x(X.right).y(Y.bottom);
	    option.tooltip().trigger(Trigger.item).formatter("{a} : {b}");
	    option.toolbox().show(true).feature(Tool.restore, Tool.saveAsImage);
	    option.legend("家人", "朋友").legend().x(X.left);
	
	    //数据
	    Force force = new Force("人物关系");
	    force.categories("人物", "家人", "朋友");
	    force.itemStyle().normal()
	            .label(new Label().show(true).textStyle(new TextStyle().color("#333")))
	            .nodeStyle().brushType(BrushType.both).color("rgba(255,215,0,0.4)").borderWidth(1);
	
	    force.itemStyle().emphasis()
	            .linkStyle(new LinkStyle())
	            .nodeStyle(new NodeStyle())
	            .label().show(true);
	    force.useWorker(false).minRadius(15).maxRadius(25).gravity(1.1).scaling(1.1).linkSymbol(Symbol.arrow);
    
		ArrayList<AsmBlockModel> blockList = funcModel.getBlockList();
		int blockListSize = blockList.size();
		
		force.nodes(new Node(1, "1", 15));
	    force.nodes(new Node(2, "1-2", 10));
	    force.links(new Link(1, "1-2", 10));
	    force.nodes(new Node(1, "2", 15));
		/*
		for (int i = 1; i < blockListSize; i++) {
			AsmBlockModel block = blockList.get(i);
			force.nodes(new Node(1, ""+block.getbNo(), 15));
			System.out.println("force.nodes(new Node(1, \""+block.getbNo()+"\", 15));");
			
			ArrayList<AsmBlockModel> subBlockSet = block.getSubBlockSet();
			for (int j = 0; j < subBlockSet.size(); j++) { 
				AsmBlockModel sublock = subBlockSet.get(j);
				force.nodes(new Node(2, block.getbNo()+"-"+sublock.getbNo(), 10));
				force.links(new Link(""+block.getbNo(), block.getbNo()+"-"+sublock.getbNo(), 10));
				
				System.out.println("force.nodes(new Node(2, \""+block.getbNo()+"-"+sublock.getbNo()+"\", 10));");
				System.out.println("force.links(new Link(\""+block.getbNo()+"\", \""+block.getbNo()+"-"+sublock.getbNo()+"\", 10));");
			}
		}*/
		option.series(force);
	    option.exportToHtml("force1.html");
	    browser.setUrl("file://d:/tmp/force1.html");
	    // option.view();
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
