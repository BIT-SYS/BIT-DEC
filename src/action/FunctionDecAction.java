package action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import utils.Global;
import view.AdvancedCodeView;
import view.CFGView;
import view.FuncsView;
import core.disassembler.AsmAdCode;
import core.disassembler.model.AsmFunc;

public class FunctionDecAction extends Action implements IWorkbenchAction, Runnable {
	private IWorkbenchWindow window = null;
	public FunctionDecAction(IWorkbenchWindow window) {
		this.setText("Function Call Graph");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Global.PLUGIN_ID, "icons/c.jpg"));
		this.setToolTipText("Function Call Graph");
		this.window = window;
	}
	
	@Override
	public void run() {
		if(this.window == null) return ;
		FuncsView funcsView = (FuncsView) Global.findView(Global.VIEW_FUNCSVIEW);
		Shell shell = this.window.getShell();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		dialog.setElements(funcsView.getList().getItems());
		/*
		 * 选择一个函数进行控制流和高级代码的输出
		 */
		dialog.setTitle("Which function do you want to dec");
		if (dialog.open() != Window.OK) {
			return;
		}
		Object[] result = dialog.getResult();
		if(result[0]!=null){
			String funcName = (String)result[0];
			decAction(funcName);
		}
	}
	
	/**
	 * 对某个函数进行控制流构建和高级代码的输出
	 * @param funcName
	 */
	public static void decAction(String funcName){
		try {
			AsmFunc funcModel = Global.FUNCMAP.get(funcName);
			//==============构建控制流图================//
			CFGView graphView = (CFGView)Global.findView(Global.VIEW_CGF); 
			graphView.drawControlFlowGraph(funcModel);
			//==============输出高级代码================//
			//AsmAdCode showHighCode = new AsmAdCode(); 
			//String highcodeContent =  showHighCode.cfgAna(funcName);
			//AdvancedCodeView adCodeView = (AdvancedCodeView) Global.findView(Global.VIEW_ADVANCEDCODE);
			//adCodeView.init();
			//adCodeView.showContent(highcodeContent, 0);
		} catch (Exception e) {
			//MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "提示", e.toString());
			Global.printer.println("无法处理函数"+funcName);
		}
	}

	@Override
	public void dispose() {
		window = null;
	}
}
