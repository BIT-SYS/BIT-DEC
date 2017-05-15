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
import org.eclipse.ui.console.MessageConsoleStream;

import view.ConsoleFactory;
import app.Activator;
import common.Global;

public class Sodump {
	private String apkPath  = "";
	private String toolPath = "";
	private String tmpPath  = "";
	private String libPath  = "";
	private String so2asm   = "";
	
	public Sodump(String apkPath){
		this.apkPath  = apkPath;
		this.toolPath = Global.TOOLPATH;
		this.tmpPath  = this.apkPath + "\\.tmp";
		this.libPath  = this.tmpPath + "\\lib";
		this.so2asm   = this.apkPath + "\\so2asm";
	}
	
	public void run(){
		//MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
		Global.printer.println("sodumping...");
		File so = new File(this.libPath);
		if(!so.exists()){
			Global.printer.println("there's no so files.");
			return ;
		}
		try {
			so2asmDir(new File(this.libPath), new File(this.so2asm));
		} 
		catch (FileNotFoundException e) {e.printStackTrace(); return ;}
		Global.printer.println("sodumped completly.");
	}
	
	public void so2asmDir(File src,File dst) throws FileNotFoundException{
		if(!src.isDirectory()) return ;
		for(File file :src.listFiles()){
			if(file.isFile()){
				if(!dst.exists()) dst.mkdirs();
				disassemble(file, dst.getAbsolutePath());
			}
			else{
				String tmpPath = dst+"\\"+file.getName();
				so2asmDir(file, new File(tmpPath));
			}
		}
	}
	
	public void disassemble(File file,String so2cPath){
		if(!file.getName().endsWith(".so")) return ;
		String sofilePath = file.getAbsolutePath();
		try {
			String cmd = this.toolPath+"arm-eabi-objdump.exe -d \""+sofilePath+"\" >> \""+so2cPath+"\\"+file.getName().replace(".so", ".asm")+"\"";
			Global.sysCmd(cmd);
			System.out.println(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
