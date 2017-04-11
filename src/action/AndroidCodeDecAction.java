package action;

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
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import utils.PathTools;
import utils.ZipUtils;
import view.ConsoleFactory;

public class AndroidCodeDecAction extends Action implements IWorkbenchAction{

	public String projectPath = "";
	public String toolsFileUrl="";
	public String dexFilePath="";
	private IWorkbenchWindow workbenchWindow;
	MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
	
	public AndroidCodeDecAction(IWorkbenchWindow window) {
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		this.setText("Generate Java Code");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				 "BIT_DEC", "icons/sample.gif"));
		this.setToolTipText("Generate Java Code");
	}
	
	public void run() {
		
		this.projectPath = PathTools.getProjectPath(workbenchWindow);
		this.toolsFileUrl = PathTools.getToolsPath();
		if  (this.workbenchWindow  !=   null )  {
			this.printer.println("parsing the classes.dex...");
			this.dexFilePath = projectPath+"/classes.dex";
			try {
				Process p = null;
				p = Runtime.getRuntime().exec(toolsFileUrl+"/dex2jar-0.0.9.15/d2j-dex2jar.bat" +
												" -f "+this.dexFilePath+
												" -o "+this.projectPath+"/classes-dex2jar.jar");
				System.out.println(toolsFileUrl+"/dex2jar-0.0.9.15/d2j-dex2jar.bat" +
									" -f "+this.dexFilePath+
									" -o "+this.projectPath+"/classes-dex2jar.jar");
				StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
	            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
	            errorGobbler.start();
	            outputGobbler.start();
	            p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
				printer.println("parse classes.dex error.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String dex2jarPath = this.projectPath+"/classes-dex2jar.jar";
			printer.println("parse classes.dex to "+dex2jarPath+" successfully");
			
			//解压classes-dex2jar.jar
			printer.println("extracting " +dex2jarPath+"...");
			dex2jarPath.replace(".jar", ".zip");
			File directory = new File(this.projectPath+"/classes");
			directory.mkdir();
			try {
				ZipUtils.decompress(dex2jarPath, directory.getAbsolutePath());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			printer.println(dex2jarPath+" extracte has completed.");
			
			//反编译classes
			printer.println("decompiling *.class...");
			File jadBat = new File(toolsFileUrl+"//jad//jad.bat");
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jadBat)));
				bw.write(toolsFileUrl+"//jad//jad.exe -r -ff -d "+projectPath+"//src -s java "+projectPath+"//classes/**/*.class");
				System.out.println(toolsFileUrl+"//jad//jad.exe -r -ff -d "+projectPath+"//src -s java "+projectPath+"//classes/**/*.class");
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Process p = null;
				p = Runtime.getRuntime().exec(toolsFileUrl+"//jad//jad.bat");
				StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
	            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
	            errorGobbler.start();
	            outputGobbler.start();
	            p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
				printer.println("decompile .class error！！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				File jadFile = new File(toolsFileUrl+"//jad//jad.bat");
				jadFile.delete();
			}
			printer.println(".class has decompiled completed, please refresh the project.");
		}
	}
	
	@Override
	public void dispose() {
		workbenchWindow  =   null ;
	}
}


//保证exec成功完成类
class StreamGobbler extends Thread {
	 InputStream is;
	 String type;
	 StreamGobbler(InputStream is, String type) {
	  this.is = is;
	  this.type = type;
	 }

	 public void run() {
	  try {
	   InputStreamReader isr = new InputStreamReader(is);
	   BufferedReader br = new BufferedReader(isr);
	   String line = null;
	   while ((line = br.readLine()) != null) {
	   }
	  } catch (IOException ioe) {
	   ioe.printStackTrace();
	  }
	 }
	}

