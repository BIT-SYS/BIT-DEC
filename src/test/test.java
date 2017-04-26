package test;

import utils.Constant;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.series.Force;
import com.github.abel533.echarts.series.force.Link;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.style.LinkStyle;
import com.github.abel533.echarts.style.NodeStyle;
import com.github.abel533.echarts.style.TextStyle;
import com.github.abel533.echarts.util.EnhancedOption;

public class test {

	
	public void run(){
	    //地址：http://echarts.baidu.com/doc/example/force1.html
		EnhancedOption option = new EnhancedOption();
		EnhancedOption option1 = new EnhancedOption();
		EnhancedOption option2 = new EnhancedOption();
		EnhancedOption option3 = new EnhancedOption();
		EnhancedOption option4 = new EnhancedOption();
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
	    
	    force.nodes(new Node(1, "1", 15));
	    force.nodes(new Node(2, "1-1", 10));
	    force.nodes(new Node(2, "1-2", 10));
	    force.links(new Link("1", "1-1", 10));
	    force.links(new Link("1", "1-2", 10));
	    force.nodes(new Node(1, "2", 15));
	
	    option.series(force);
	    option.exportToHtml(Constant.OUTPUTFOLDER+"CFG.html");
	}
}
