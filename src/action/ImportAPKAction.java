package action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import common.Global;
import common.PathTools;
import core.APKProcessor.ApkProcessor;


public class ImportAPKAction extends Action implements   IWorkbenchAction,Runnable {
	
	private IWorkbenchWindow workbenchWindow;
	
	public ImportAPKAction(IWorkbenchWindow window) 
	{   
	   if  (window == null ){
		  throw new IllegalArgumentException();
	   } 
	   this.workbenchWindow = window; 
	   this.setText("Import Android APK"); 
	}
	
	@Override
	public void run() 
	{ 
		if  (workbenchWindow != null )  {
			String projectPath = PathTools.getProjectPath(workbenchWindow);
			
			Shell shell = workbenchWindow.getShell();
			FileDialog dialog = new FileDialog (shell, SWT.OPEN);
			String[] type = {"*.apk","*.hex"};
			dialog.setFilterExtensions(type);
			dialog.open();
			
			String apk      = dialog.getFileName();
			String apkName  = apk.substring(0, apk.lastIndexOf("."));
			String fileType = apk.substring(apk.lastIndexOf(".")+1).toLowerCase();
			String filePath = dialog.getFilterPath()+"\\"+apk;

			if(Global.APKPATH.containsKey(apk))  Global.printer.println()

			switch(fileType){
				case "apk":{
					ApkProcessor apkPreprocessor = new ApkProcessor(projectPath,filePath,this.workbenchWindow);
					Thread apkpreThread = new Thread(apkPreprocessor);
					apkpreThread.start();
					break;
				}
				case "8051":{break;}
				case "PIC":{break;}
			}
		} 
		
	}

	@Override
	public void dispose() {
		workbenchWindow  =   null ;
	}
}
