package core.APKProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import common.Global;
import common.PathTools;
import common.ZipUtils;

public class getSourceCode extends Action implements IWorkbenchAction{
	private IWorkbenchWindow workbenchWindow;
	private String apkPath = "";
	private String tmpPath = "";
	private String dexFilePath="";
	private String dex2jarPath = "";
	private String classPath = "";
	private String javaPath = "";
	private String tmpJavaPath = "";
	private String D2J_BAT = Global.D2J_BAT;
	private String JAD_BAT = Global.JAD_BAT;
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
		this.tmpJavaPath = Global.TMP+"/JavaCode";
		
		//delete decompileClass & JavaSourceCode of other apk
		File dir = new File(Global.TMP);
		Global.delDir(dir);
		dir.mkdir();
	}
	
	public void dex2jar(){
		/////////////////////////////////////////////////////////
		//dex ->jar
		Global.printer.println("parsing the classes.dex...");
		try {
			String cmd = Global.D2J_BAT +" -f \""+this.dexFilePath+"\" -o \""+this.dex2jarPath+'\"';
			Process p = Runtime.getRuntime().exec(cmd);
			System.out.println(cmd);
			StreamGobbler errorGobbler  = new StreamGobbler(p.getErrorStream(), "Error");            
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			Global.printer.println("parse classes.dex error.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Global.printer.println("parse classes.dex successfully");
	}
	
	public void jar2java(){
		//////////////////////////////////////////////////////////////
		//解压classes-dex2jar.jar
		Global.printer.println("extracting " +dex2jarPath+"...");
		//dex2jarPath.replace(".jar", ".zip");
		try {
			Global.unZipJar(dex2jarPath, classPath);
		} catch (Exception e2) {
			Global.printer.println(e2.getMessage());
			return ;
		}
		Global.printer.println(dex2jarPath+" extracte has completed.");
		
		//反编译classes
		Global.printer.println("decompiling *.class...");
		try {
			new File(this.javaPath).mkdirs();
			String cmd = JAD_EXE+" -r -ff -d \""+this.tmpJavaPath+"\" -s java \""+this.classPath+"/**/*.class\"";
			Process p = Runtime.getRuntime().exec(cmd);
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();
			p.waitFor();

			//JAD can't handle chinese path, so use tmp dir
			Global.copyDir(this.tmpJavaPath, this.javaPath);
		} catch (IOException e) {
			e.printStackTrace();
			Global.printer.println("decompile .class error！！");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Global.printer.println(".class has decompiled completed");
	}
	
	public void run() {
		dex2jar();
		jar2java();
	}
	
	@Override
	public void dispose() {
		workbenchWindow  =   null ;
	}

}

