package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.ui.console.MessageConsoleStream;

import utils.ZipUtils;
import view.ConsoleFactory;


public class TimeClass {
	
	
	public static String apkname = "com.sina.weibo_102009";
	public static String toolsFileUrl="E:\\workspace\\bit-dec\\tools";
	public static String apkPath = "F:\\test\\classTime\\apks\\"+apkname+".apk";
	
	public static void main(String[] args) throws Exception { 
		String unzipPath = apkPath+".unzip";
		apkPath.replace(".apk", ".zip");
		String dexPath = unzipPath+"\\classes.dex";
		String dex2jarPath = unzipPath+"/classes-dex2jar.jar";
		String classesPath = unzipPath+"/classes";
		String soPath = unzipPath+"\\lib\\armeabi";
		String so2cPath = unzipPath+"\\so2asm";
		
		long start = System.currentTimeMillis();
		ZipUtils.decompress(new File(apkPath),new File(unzipPath));
		dex2jar(dexPath,unzipPath);
		dex2jarPath.replace(".jar", ".zip");
		ZipUtils.decompress(dex2jarPath,classesPath);
		jad(unzipPath);
		long end = System.currentTimeMillis();
		long javaTime = end - start;
		System.out.println(apkname+"-Java反编译耗时:" + javaTime/1000+"秒");
		
		start = System.currentTimeMillis();
		File soDiassembler = new File(so2cPath);
		soDiassembler.mkdir();
		listSo(new File(soPath), so2cPath);
		end = System.currentTimeMillis();
		long soTime = end - start;
		System.out.println(apkname+"-so反汇编耗时:" + soTime/1000+"秒");
		System.out.println(apkname+"-总共反编译耗时："+(javaTime+soTime)/1000+"秒");
	}
	
	
	public static void listSo(File file,String so2cPath) throws FileNotFoundException{
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
	
	public static void disassemble(File file,String so2cPath){
		if(file.getName().endsWith(".so")){
			File fileBat = new File(file.getAbsolutePath().replace(".so", ".bat"));
			String sofilePath = file.getAbsolutePath();
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBat)));
				String sqlString = toolsFileUrl+"\\arm-eabi-objdump.exe -d \""+sofilePath+"\""+" >>"+so2cPath+"\\"+file.getName().replace(".so", ".asm");
				bw.write(sqlString);
				System.out.println(sqlString);
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Process p = null;
				p = Runtime.getRuntime().exec(fileBat.getAbsolutePath());
				StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
	            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
	            errorGobbler.start();
	            outputGobbler.start();
	            p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				fileBat.delete();
			}
		}
	}
	
	
	
	
	public static void dex2jar(String classesPath,String unzipPath){
		try {
			Process p = null;
			String sqlString = toolsFileUrl+"/dex2jar-0.0.9.15/d2j-dex2jar.bat" +
					" -f "+classesPath+
					" -o "+unzipPath+"/classes-dex2jar.jar";
			p = Runtime.getRuntime().exec(sqlString);
			System.out.println(sqlString);
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
            errorGobbler.start();
            outputGobbler.start();
            p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void jad(String unzipPath){
		File jadBat = new File(toolsFileUrl+"//jad//jad.bat");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jadBat)));
			String sqlString = toolsFileUrl+"//jad//jad.exe -r -ff -d "+unzipPath+"//src -s java "+unzipPath+"//classes/**/*.class";
			bw.write(sqlString);
			System.out.println(sqlString);
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Process p = null;
			p = Runtime.getRuntime().exec(jadBat.getAbsolutePath());
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");            
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
            errorGobbler.start();
            outputGobbler.start();
            p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			jadBat.delete();
		}
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