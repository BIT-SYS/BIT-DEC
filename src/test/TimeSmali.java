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

public class TimeSmali {
	
	public static String apkname = "com.kugou.android_030013";
	public static String toolsFileUrl="E:\\workspace\\bit-dec\\tools";
	public static String apkPath = "F:\\test\\smaliTime\\apks\\"+apkname+".apk";
	public static String apk2smaliPath = "F:\\test\\smaliTime\\smalis";
	public static String smaliPath = apk2smaliPath+"\\"+apkname;
	public static String so2cPath = smaliPath+"\\so2asm";
	public static String soPath = smaliPath+"\\lib\\armeabi";
	public static void main(String[] args) throws FileNotFoundException {
		
		long start = System.currentTimeMillis();
		apk2smali(apkPath);
		smali2java();
		long end = System.currentTimeMillis();
		long javaTime = end - start;
		System.out.println(apkname+"-Java反编译耗时:" + javaTime/1000+"秒");
		
		start = System.currentTimeMillis();
		File soDiassembler = new File(so2cPath);
		soDiassembler.mkdir();
		TimeClass.listSo(new File(soPath), so2cPath);
		end = System.currentTimeMillis();
		long soTime = end - start;
		System.out.println(apkname+"-so反汇编耗时:" + soTime/1000+"秒");
		System.out.println(apkname+"-总共反编译耗时："+(javaTime+soTime)/1000+"秒");
	}
	
	public static void apk2smali(String apkPath){
		File jadBat = new File(toolsFileUrl+"//apk2smali.bat");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jadBat)));
			String sqlString = "java -jar "+toolsFileUrl+"\\apktool.jar d "+apkPath+" -o "+smaliPath;
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
	
	
	public static void smali2java(){
		File jadBat = new File(toolsFileUrl+"//smali2java.bat");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jadBat)));
			String sqlString = toolsFileUrl+"\\SmaliToJava.exe "+smaliPath;
			bw.write(sqlString);
			System.out.println(sqlString);
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Process p = null;
			p = Runtime.getRuntime().exec(jadBat.getAbsolutePath());
			InputStream fis = p.getInputStream();
			InputStream fise = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(fis);
			InputStreamReader isre = new InputStreamReader(fise);
			BufferedReader br = new BufferedReader(isr);
			BufferedReader bre = new BufferedReader(isre);
			String line=null;
			while((line=br.readLine())!=null) {
				System.out.println(line);
			}
			while((line = bre.readLine()) != null){
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			jadBat.delete();
		}
	}

}
