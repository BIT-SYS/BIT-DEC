package core.APKProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.ui.IWorkbenchWindow;

import common.Global;
import common.Global.printer;

public class ApkProcessor implements Runnable{
	
	private IWorkbenchWindow workbenchWindow;
	private String projectPath = "";
	private String apkName = "";
	private String apkPath = "";
	private String filePath = "";
	private String tmpPath = "";
	
	public  ApkProcessor(String projectPath, String apkName, String filePath, IWorkbenchWindow workbenchWindow){
		this.workbenchWindow = workbenchWindow;
		this.projectPath = projectPath;
		this.apkName     = apkName;
		this.apkPath     = this.projectPath+"\\"+this.apkName;
		this.tmpPath   = this.apkPath+"\\.tmp";
		this.filePath    = filePath;
	}
	
	@Override
	public void run() {
		try {
			Global.unzipFile(filePath, tmpPath);
			new getSourceCode(apkPath).run();
			new Sodump(apkPath).run();
			printer.println("APK has been preproced, please refresh the project");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	public void apk2smali(String apkfilePath){
//		URL url = Activator.getDefault().getBundle().getResource("tools");
//		try {
//			toolsFileUrl = FileLocator.toFileURL(url).toString().substring(6);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		File fileBat = new File(toolsFileUrl+"apk2smali.bat");
//		BufferedWriter bw;
//		MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBat)));
//			bw.write("java -jar "+toolsFileUrl+"apktool.jar d "+apkfilePath+" -o "+this.projectPath+"/smali");
//			System.out.println("java -jar "+toolsFileUrl+"apktool.jar d "+apkfilePath+" -o "+this.projectPath+"/smali");
//			printer.println(apkfilePath);
//			bw.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Process p = null;
//		String command = fileBat.getAbsolutePath();
//		System.out.println(command);
//		try {
//			p = Runtime.getRuntime().exec(command);
//			InputStream fis = p.getInputStream();
//			InputStream fise = p.getErrorStream();
//			InputStreamReader isr = new InputStreamReader(fis);
//			InputStreamReader isre = new InputStreamReader(fise);
//			BufferedReader br = new BufferedReader(isr);
//			BufferedReader bre = new BufferedReader(isre);
//			String line=null;
//			while((line=br.readLine())!=null) {
//				System.out.println(line);
//				printer.println(line);
//			}
//			while((line = bre.readLine()) != null){
//				System.out.println(line);
//				printer.println(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		fileBat.delete();
//	}
//	
//	
//	public void smali2java(String smaliFolderPath) {
//		File fileBat = new File(toolsFileUrl+"smali2java.bat");
//		BufferedWriter bw;
//		MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBat)));
//			bw.write(toolsFileUrl+"SmaliToJava.exe "+this.projectPath+"/smali");
//			System.out.println(toolsFileUrl+"SmaliToJava.exe "+smaliFolderPath);
//			printer =ConsoleFactory.getConsole().newMessageStream();
//			bw.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Process p = null;
//		String command = fileBat.getAbsolutePath();
//		System.out.println(command);
//		try {
//			p = Runtime.getRuntime().exec(command);
//			InputStream fis = p.getInputStream();
//			InputStream fise = p.getErrorStream();
//			InputStreamReader isr = new InputStreamReader(fis);
//			InputStreamReader isre = new InputStreamReader(fise);
//			BufferedReader br = new BufferedReader(isr);
//			BufferedReader bre = new BufferedReader(isre);
//			String line=null;
//			while((line=br.readLine())!=null) {
//				System.out.println(line);
//				printer.println(line);
//			}
//			while((line = bre.readLine()) != null){
//				System.out.println(line);
//				printer.println(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		fileBat.delete();
//	}
}
