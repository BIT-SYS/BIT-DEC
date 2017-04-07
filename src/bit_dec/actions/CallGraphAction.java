package bit_dec.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import bit_dec.callgraph.FuncParser;
import bit_dec.utils.PathTools;
import bit_dec.views.CallGraphView;

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
		//����java��so�ĺ�������ͼ
		FuncParser funcParser = new FuncParser(projectPath);
		funcParser.parse();
		//���Ʋ���ʾ��������ͼ
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IViewPart viewPart = page.showView("BIT_DEC.callgraphView");
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