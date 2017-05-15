package core.APKProcessor;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import common.Global;

public class getSourceCode extends Action implements IWorkbenchAction, Runnable{
	private IWorkbenchWindow workbenchWindow;
	private String apkPath = "";
	private String tmpPath = "";
	private String dexFilePath="";
	private String dex2jarPath = "";
	private String classPath = "";
	private String javaPath = "";
	private String tmpJavaPath = "";
	private String D2J_BAT = Global.D2J_BAT;
	private String JAD_EXE = Global.JAD_EXE;
	
	public getSourceCode(IWorkbenchWindow window) {
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		this.setText("Generate Java Code");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				 "BIT_DEC", "icons/sample.gif"));
		this.setToolTipText("Generate Java Code");
	}
	
	public getSourceCode(String apkPath) {
		this.apkPath     = apkPath;
		this.javaPath    = this.apkPath+"/JavaCode";
		this.tmpPath     = this.apkPath+"/.tmp";
		this.dexFilePath = this.tmpPath+"/classes.dex";
		this.dex2jarPath = this.tmpPath+"/classes-dex2jar.jar";
		this.classPath   = Global.TMP+"/DecompileClass";
		this.tmpJavaPath = Global.TMP+"/JAVACODE";
		
		//delete decompileClass & JavaSourceCode of other apk
		File dir = new File(Global.TMP);
		Global.delDir(dir);
		dir.mkdir();
	}
	
	//dex ->jar
	public void dex2jar() throws Exception{
		String cmd = Global.D2J_BAT +" -f \""+this.dexFilePath+"\" -o \""+this.dex2jarPath+'\"';
		Global.sysCmd(cmd);
	}
	
	//jar->class
	public void jar2class() throws Exception{
		Global.unZipJar(dex2jarPath, classPath);
	}
	
	//class->java
	public void class2java() throws Exception{
		new File(this.javaPath).mkdirs();
		String cmd = JAD_EXE+" -r -ff -d \""+this.tmpJavaPath+"\" -s java \""+this.classPath+"/**/*.class\"";
		Global.sysCmd(cmd);
		//JAD can't handle chinese path, so use tmp dir
		Global.copyDir(this.tmpJavaPath, this.javaPath);
	}
	
	public void run() {
		try{
			Global.printer.print("\nparsing classes.dex -> classes-dex2jar.jar ...");
			dex2jar();
			Global.printer.print("succeed!!");
			Global.printer.print("\ndecompressing classes-dex2jar.jar -> *.class ...");
			jar2class();
			Global.printer.print("succeed!!");
			Global.printer.print("\ndecompiling *.class -> *.java...");
			class2java();
			Global.printer.print("succeed!!");
			Global.getSourceCodeSucceed = true;
		}
		catch(Exception e){
			Global.printer.print("error!!");
		}
	}
	
	@Override
	public void dispose() {
		workbenchWindow  =   null ;
	}

}

