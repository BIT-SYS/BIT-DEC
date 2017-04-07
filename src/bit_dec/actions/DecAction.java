package bit_dec.actions;


import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import bit_dec.dissambler.so.AsmAdCode;
import bit_dec.dissambler.so.AsmFuncModel;
import bit_dec.dissambler.so.AsmStructAna;
import bit_dec.dissambler.so.AsmTextSectionStruct;
import bit_dec.views.AdCodeView;
import bit_dec.views.CFGView;
import bit_dec.views.FuncsView;

public class DecAction extends Action implements IWorkbenchAction, Runnable {
	
	/**
	 * 对某个函数进行控制流构建和高级代码的输出
	 * @param funcName
	 */
	public static void decAction(String funcName,IWorkbenchPage workbenchPage){
		
		HashMap<String, AsmFuncModel> funcMap = AsmTextSectionStruct.textSectionModel.getFuncMap();
		AsmStructAna structAna = new AsmStructAna();
		AsmFuncModel funcModel = structAna.genCfg(funcMap.get(funcName));
		//==============构建控制流图================//
		CFGView graphView = (CFGView)workbenchPage.findView("BIT_DEC.cfgView");
		graphView.drawCFG(funcModel);
		//==============输出高级代码================//
		AsmAdCode showHighCode = new AsmAdCode(); 
		try {
			String highcodeContent =  showHighCode.cfgAna(funcName);
			AdCodeView adCodeView = (AdCodeView) workbenchPage.findView("BIT_DEC.advanced_code");
			adCodeView.init();
			adCodeView.showContent(highcodeContent, 0);
		} catch (Exception e) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "提示",
					e.toString());
		}
	}
	
	private IWorkbenchWindow workbenchWindow;

	public DecAction(IWorkbenchWindow window) {
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		this.setText("Generate C Code");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				 "BIT_DEC", "icons/c.jpg"));
		this.setToolTipText("Generate C Code");
	}
	
	@Override
	public void run() {
		if (workbenchWindow != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			FuncsView funcsView = (FuncsView)page.findView("BIT_DEC.functionsView");
			Shell shell = workbenchWindow.getShell();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					shell, new LabelProvider());
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
				decAction(funcName,page);
			}
		}
	}


	@Override
	public void dispose() {
		workbenchWindow = null;
	}
}
