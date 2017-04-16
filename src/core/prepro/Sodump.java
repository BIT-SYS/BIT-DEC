package core.prepro;

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
import org.eclipse.ui.console.MessageConsoleStream;

import view.ConsoleFactory;
import app.Activator;

public class Sodump {
	
	private String projectPath = "";
	private String toolsFileUrl = "";
	
	public Sodump(String projectPath){
		this.projectPath = projectPath;
		URL url = Activator.getDefault().getBundle().getResource("tools");
		try {
			this.toolsFileUrl = FileLocator.toFileURL(url).toString().substring(6);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
		printer.println("sodumping...");
		File[] apkFiles = new File(projectPath).listFiles();
		int k;
		for(k=0;k<apkFiles.length;k++){
			if(apkFiles[k].getName().equals("lib")){
				try {
					File soDiassembler = new File(projectPath+"/so2asm");
					soDiassembler.mkdir();
					listSo(apkFiles[k],soDiassembler.getAbsolutePath());
				} catch (FileNotFoundException e) {
				e.printStackTrace();
				}
				break;
			}
		}
		if(k == apkFiles.length){
			printer.println("there's no so files.");
		}
		else{
			printer.println("sodumped completly.");
		}
		printer.println("APK has been preproced, please refresh the project");
	}
	
	public void listSo(File file,String so2cPath) throws FileNotFoundException{
		File[] files = file.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile()){
				disassemble(files[i],so2cPath);
			}
			else{
				String so2cInnerPath = so2cPath+"\\"+files[i].getName();
				new File(so2cInnerPath).mkdir();
				listSo(files[i],so2cInnerPath);
			}
		}
	}
	
	public void disassemble(File file,String so2cPath){
		if(file.getName().endsWith(".so")){
			File fileBat = new File(file.getAbsolutePath().replace(".so", ".bat"));
			BufferedWriter bw;
			String sofilePath = file.getAbsolutePath();
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBat)));
				bw.write(this.toolsFileUrl+"arm-eabi-objdump.exe -d \""+sofilePath+"\""+" >>"+so2cPath+"\\"+file.getName().replace(".so", ".asm"));
				System.out.println(this.toolsFileUrl+"arm-eabi-objdump.exe -d \""+sofilePath+"\""+" >>"+so2cPath+"\\"+file.getName().replace(".so", ".asm"));
				MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
				printer.println(sofilePath);
				bw.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Process p = null;
			String command = fileBat.getAbsolutePath();
			System.out.println(command);
			try {
				p = Runtime.getRuntime().exec(command);
				InputStream fis = p.getInputStream();
				InputStream fise = p.getErrorStream();
				InputStreamReader isr = new InputStreamReader(fis);
				InputStreamReader isre = new InputStreamReader(fise);
				BufferedReader br = new BufferedReader(isr);
				BufferedReader bre = new BufferedReader(isre);
				String line=null;
				while((line=br.readLine())!=null) {
				}
				while((line = bre.readLine()) != null){
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileBat.delete();
		}
	}

}
