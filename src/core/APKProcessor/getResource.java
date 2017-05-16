package core.apkprocessor;

import java.io.File;

import org.eclipse.ui.IWorkbenchWindow;

import utils.Global;

public class getResource implements Runnable{
	private IWorkbenchWindow workbenchWindow;
	private String apktoolPath="";
	private String apkName = "";
	private String apkPath = "";
	private String filePath = "";
	private String tmpapkPath = "";
	private String tmpResPath = "";
	private String tmpapktoolPath = "";
	
	public getResource(String apkPath, String filePath, String apkName){
		this.apkPath  = apkPath;
		this.filePath = filePath;
		this.apktoolPath   = Global.APKTOOL;
		this.apkName = apkName;
		this.tmpapktoolPath = Global.TMP+"\\apktool.jar";
		this.tmpapkPath = Global.TMP+"\\1.apk";
		this.tmpResPath = Global.TMP+"\\RESOURCE";
		new File(this.tmpapkPath).delete();
		File res = new File(this.tmpResPath);
		Global.delDir(res);
		res.mkdirs();
	} 
	
	public void run(){
		//Global.copyFile(this.filePath, this.tmpapkPath);
		//Global.copyFile(this.apktoolPath, this.tmpapktoolPath);
		Global.printer.print("\ngetting resource from  "+apkName+" ... ");
		String cmd = "java -jar \""+this.apktoolPath+"\" d \""+this.filePath+"\" -f -o \""+ this.apkPath+"\"";
		//Global.copyDir(this.tmpResPath, this.apkPath);
		try {
			Global.sysCmd(cmd );//,Global.printer);
			Global.printer.print("succeed!!");
		} catch (Exception e) {
			Global.printer.print("error!!");
			//Global.printer.print(e.getMessage());
		}
	}
	
}
