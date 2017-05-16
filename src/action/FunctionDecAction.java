package action;

import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import utils.Global;
import view.AdvancedCodeView;
import view.CFGView;
import view.ConsoleFactory;
import view.FuncsView;
import core.dissambler.AsmAdCode;
import core.dissambler.AsmStructAna;
import core.dissambler.AsmTextSectionStruct;
import core.dissambler.model.AsmFunc;

public class FunctionDecAction extends Action implements IWorkbenchAction, Runnable {
	
	public FunctionDecAction(IWorkbenchWindow window) {
		this.setText("Function Call Graph");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Global.PLUGIN_ID, "icons/c.jpg"));
		this.setToolTipText("Function Call Graph");
	}
	
	@Override
	public void run() {
		FuncsView funcsView = (FuncsView) Global.findView(Global.VIEW_FUNCSVIEW);
		//Shell shell = workbenchWindow.getShell();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(, new LabelProvider());
		dialog.setElements(funcsView.getList());
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
		HashMap<String, AsmFunc> funcMap = AsmTextSectionStruct.textSectionModel.getFuncMap();
		AsmStructAna structAna = new AsmStructAna();
		try {
			AsmFunc funcModel = structAna.genCfg(funcMap.get(funcName));
			//AsmFuncModel funcModel = funcMap.get(funcName);
			//==============构建控制流图================//
			CFGView graphView = (CFGView)Global.findView(Global.VIEW_CGF); 
			graphView.drawCFG(funcModel);
			//==============输出高级代码================//
			AsmAdCode showHighCode = new AsmAdCode(); 
			String highcodeContent =  showHighCode.cfgAna(funcName);
			AdvancedCodeView adCodeView = (AdvancedCodeView) Global.findView(Global.VIEW_ADVANCEDCODE);
			adCodeView.init();
			adCodeView.showContent(highcodeContent, 0);
		} catch (Exception e) {
			//MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "提示", e.toString());
			Global.printer.println("无法处理函数"+funcName);
		}
	}

	@Override
	public void dispose() {
		workbenchWindow = null;
	}
}
