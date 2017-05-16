package action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import utils.Global;
import utils.PathTools;
import core.apkprocessor.ApkProcessor;


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
		if(workbenchWindow == null )  return ;
		String projectPath = PathTools.getProjectPath(workbenchWindow);
		
		Shell shell = workbenchWindow.getShell();
		FileDialog dialog = new FileDialog (shell, SWT.OPEN);
		String[] type = {"*.apk","*.hex"};
		dialog.setFilterExtensions(type);
		dialog.open();
		
		String file     = dialog.getFileName();
		String fileType = file.substring(file.lastIndexOf(".")+1);
		String fileName = file.substring(0, file.lastIndexOf("."));
		String filePath = dialog.getFilterPath()+"\\"+file;
		
		Global.printer.println("imported "+file);
		switch(fileType){
			case "apk":{
				String apkPath = projectPath+"\\"+fileName;
				if(Global.APKPATH.contains(apkPath)){
					Global.printer.println(file+" is already in project "+projectPath.substring(projectPath.lastIndexOf("\\")+1));
					return ;
				}
				Global.APKPATH.add(apkPath);
				ApkProcessor apkPreprocessor = new ApkProcessor(projectPath, fileName, filePath,this.workbenchWindow);
				Thread apkpreThread = new Thread(apkPreprocessor);
				apkpreThread.start();
				break;
			}
			case "8051":{break;}
			case "PIC":{break;}
		}
		
	}

	@Override
	public void dispose() {
		workbenchWindow  =   null ;
	}
}
