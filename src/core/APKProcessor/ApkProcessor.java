package core.APKProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.MessageConsoleStream;

import utils.FileTools;
import utils.ZipUtils;
import view.ConsoleFactory;
import app.Activator;

public class ApkProcessor implements Runnable{
	
	private IWorkbenchWindow workbenchWindow;
	private String projectPath = "";
	private String filePath = "";
	private String toolsFileUrl = "";
	
	public  ApkProcessor(String projectPath,String filePath,IWorkbenchWindow workbenchWindow){
		this.workbenchWindow = workbenchWindow;
		this.projectPath = projectPath;
		this.filePath = filePath;
	}
	
	public void decompressionApk() throws Exception{
//		this.apk2smali(this.filePath);
//		this.smali2java(this.projectPath+"/smali");
//		FileTools.deleteFile(new File(this.projectPath+"/smali"));
		
		MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
		printer.println(this.filePath+" is being under decompression...");
		this.filePath.replace(".apk", ".zip");
		ZipUtils.decompress(this.filePath, this.projectPath);
		printer.println(this.filePath+" decompressed completly.");
	}
	
	@Override
	public void run() {
		try {
			this.decompressionApk();
			
			AndroidCodeDecAction androidCodeDecAction = new AndroidCodeDecAction(workbenchWindow);
			androidCodeDecAction.run();
			Sodump sodump = new Sodump(projectPath);
			sodump.run();
			//System.out.println("tinymintinymintinymintinymintinymintinymintinymintinymin");
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
