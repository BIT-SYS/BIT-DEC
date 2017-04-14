package action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import prepro.ApkPreprocessor;
import utils.PathTools;


public class OpenAction extends Action implements   IWorkbenchAction,Runnable {
	
	private IWorkbenchWindow workbenchWindow;
	private String projectPath = "";
	private String filePath = "";
	private String apkName = "";
	
	
	public OpenAction(IWorkbenchWindow window) 
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
		this.projectPath = PathTools.getProjectPath(workbenchWindow);
		if  (workbenchWindow != null )  {
			Shell shell = workbenchWindow.getShell();
			FileDialog dialog = new FileDialog (shell, SWT.OPEN);
			String[] type = {"*.apk","*.hex"};
			dialog.setFilterExtensions(type);
			dialog.open();
			
			apkName = dialog.getFileName();
			filePath = dialog.getFilterPath()+"\\"+apkName;
			String fileType = filePath.substring(filePath.lastIndexOf(".")+1);
			
			switch(fileType){
				case "apk":{
					ApkPreprocessor apkPreprocessor = new ApkPreprocessor(projectPath,filePath,this.workbenchWindow);
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
