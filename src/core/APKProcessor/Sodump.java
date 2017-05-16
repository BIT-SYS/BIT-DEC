package core.apkprocessor;

import java.io.File;
import java.io.FileNotFoundException;

import utils.Global;

public class Sodump implements Runnable{
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
		this.so2asm   = this.apkPath + "\\SO2ASM";
	}
	
	public void run(){
		//MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
		Global.printer.print("\nsodumping *.so -> *.asm ... ");
		File so = new File(this.libPath);
		if(!so.exists()){
			Global.printer.println("there's no so files.");
			Global.SOdumpSucceed = true;
			return ;
		}
		try {
			so2asmDir(new File(this.libPath), new File(this.so2asm));
			Global.SOdumpSucceed = true;
		} 
		catch (FileNotFoundException e) {e.printStackTrace(); return ;}
		catch (Exception e) {e.printStackTrace(); return ;}
		Global.printer.print("succeed!!");
	}
	
	public void so2asmDir(File src,File dst) throws FileNotFoundException, Exception{
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
	
	public void disassemble(File file,String so2cPath) throws Exception{
		if(!file.getName().endsWith(".so")) return ;
		String sofilePath = file.getAbsolutePath();
		String cmd = this.toolPath+"arm-eabi-objdump.exe -d \""+sofilePath+"\"";
		Global.sysCmd(cmd, so2cPath+"\\"+file.getName().replace(".so", ".asm"));
		//System.out.println(cmd);
	}
}
