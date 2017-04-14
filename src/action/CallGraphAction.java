package action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import utils.PathTools;
import view.CallGraph;
import callgraph.FuncParser;
import sys.Constant;
public class CallGraphAction extends Action implements IWorkbenchAction{
	
	private  IWorkbenchWindow workbenchWindow;
	public CallGraphAction(IWorkbenchWindow window) {
		// TODO Auto-generated constructor stub
		if  (window  ==   null ){
			  throw   new  IllegalArgumentException();
		   } 
		this.setText("Generate Call Graph");
		   this.workbenchWindow = window; 
		   setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
 "BIT_DEC", "icons/graph.jpg"));
		   this.setToolTipText("Generate Call Graph");
	}
	
	
	@Override
	public void run(){
		String projectPath = PathTools.getProjectPath(workbenchWindow);
		//构建java和so的函数调用图
		FuncParser funcParser = new FuncParser(projectPath);
		funcParser.parse();
		//绘制并显示函数调用图
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IViewPart viewPart = page.showView(Constant.VIEW_CALLGRAPH);
			CallGraph callgarphView = (CallGraph)viewPart;
			callgarphView.drawCallGraph();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
