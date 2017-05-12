package action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import common.Global;
import common.PathTools;

import view.CallGraphView;
import core.callgraph.FuncParser;

public class CallGraphAction extends Action implements IWorkbenchAction{
	private  IWorkbenchWindow workbenchWindow;
	public CallGraphAction(IWorkbenchWindow window) {
		// TODO Auto-generated constructor stub
		if  (window  ==   null ){
			  throw   new  IllegalArgumentException();
		   } 
		this.setText("Generate Call Graph");
	   this.workbenchWindow = window; 
	   setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Global.PLUGIN_ID, "icons/graph.jpg"));
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
			IViewPart viewPart = page.showView(Global.VIEW_CALLGRAPH);
			CallGraphView callgarphView = (CallGraphView)viewPart;
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
