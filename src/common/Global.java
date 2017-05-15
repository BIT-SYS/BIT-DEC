package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.console.MessageConsoleStream;

import app.Activator;
import view.ConsoleFactory;

public class Global {
	public static final String PLUGIN_ID 		 = "bit-dec";
	public static final String PERSPECTIVE_ID    = "app.Perspective";
	public static final String VIEW_ADVANCEDCODE = "view.AdvancedCodeView";
	public static final String VIEW_CALLGRAPH    = "view.CallGraphView";
	public static final String VIEW_CGF          = "view.CFGView";
	public static final String VIEW_FUNCSVIEW    = "view.FuncsView";
	public static final String VIEW_NAVIGATOR    = "view.NavigatorView";
	
	//public static String PROGRAMLOCATION = Platform.getInstallLocation().getURL().getPath(); 
	public static String TMP             = System.getProperty("user.home")+"/.BIT-DEC";
	public static String PROGRAMLOCATION = "D:\\WorkSpace\\eclipse-rcp\\BIT-DEC";
	public static String OUTPUTFOLDER    = PROGRAMLOCATION + "\\resources\\";
	public static String TEMPLATE        = PROGRAMLOCATION + "\\resources\\template";
	public static String TOOLPATH        = PathTools.getToolsPath();
	public static String D2J_BAT         = TOOLPATH+"dex2jar-0.0.9.15/d2j-dex2jar.bat";
	public static String JAD_BAT         = TOOLPATH+"jad/jad.bat";
	public static String JAD_EXE         = TOOLPATH+"jad/jad.exe";
	
	public static ArrayList<String> APKPATH = new ArrayList<String>();
	public static MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
	
	public static void unzipFile(String filePath, String projectPath) throws Exception{
//		this.apk2smali(this.filePath);
//		this.smali2java(this.projectPath+"/smali");
//		FileTools.deleteFile(new File(this.projectPath+"/smali"));
				
		Global.printer.println(filePath+" is being under decompression...");
		long startTime=System.currentTimeMillis();  
        ZipInputStream Zin=new ZipInputStream(new FileInputStream(filePath));
        BufferedInputStream Bin=new BufferedInputStream(Zin);  
        File Fout=null;  
        ZipEntry entry;  

        while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){  
            Fout=new File(projectPath,entry.getName());  
            if(!Fout.exists()){  
                (new File(Fout.getParent())).mkdirs();  
            }  
            FileOutputStream out=new FileOutputStream(Fout);  
            BufferedOutputStream Bout=new BufferedOutputStream(out);  
            int b;  
            while((b=Bin.read())!=-1){  
                Bout.write(b);  
            }  
            Bout.close();  
            out.close();   
        }  
        Bin.close();  
        Zin.close();  

        long endTime=System.currentTimeMillis();  
        Global.printer.println("decompression used time： "+(endTime-startTime)+" ms");  
 
		//ZipUtils.decompress(this.filePath, this.projectPath);
		Global.printer.println(filePath+" decompressed completly.");
	}
	
	public static  void unZipJar(String filePath, String outDir) throws IOException{  
        JarFile jarFile = new JarFile(filePath);  
        Enumeration<JarEntry> jarEntrys = jarFile.entries(); 
        JarEntry jarEntry = null;
        while(jarEntrys.hasMoreElements()){
        	jarEntry = jarEntrys.nextElement();
        	if(jarEntry.isDirectory()) continue;
            String outFileName =  outDir+"/"+ jarEntry.getName();   
            File f = new File(outFileName);  
            File fp = new File(f.getParent());
            if(!fp.exists()) fp.mkdirs();
            OutputStream ops = new BufferedOutputStream(new FileOutputStream(f));  
            InputStream  ips = jarFile.getInputStream(jarEntry);
            byte[] buffer = new byte[1024];   
            int nBytes = 0;   
            while ((nBytes = ips.read(buffer)) > 0){   
                ops.write(buffer, 0, nBytes);   
            }   
            ops.flush();   
            ops.close();   
            ips.close();   
        }  
    }  
	
	public static void delDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    for (File file: path.listFiles())
	        delDir(file);  
	    path.delete();  
	}  
	

	public static void copyDir(String src, String des) {  
        File file1=new File(src);  
        File file2=new File(des);  
        if(!file2.exists()){  
            file2.mkdirs();  
        }  
        for (File f : file1.listFiles()) {  
            if(f.isFile()) 			  copyFile(f.getPath(),des+"\\"+f.getName()); 
            else if(f.isDirectory())  copyDir (f.getPath(),des+"\\"+f.getName());  
        }  
          
    }  
	  
	public static void copyFile(String src, String des) {  
        BufferedReader br=null;  
        PrintStream ps=null;   
        try {  
            br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));  
            ps=new PrintStream(new FileOutputStream(des));  
            String s=null;  
            while((s=br.readLine())!=null){  
                ps.println(s);  
                ps.flush();  
            }  
              
        } 
        catch (FileNotFoundException e) { e.printStackTrace();}
        catch (IOException e) {          e.printStackTrace(); }
        finally{  
                try {  
                    if(br!=null)  br.close();  
                    if(ps!=null)  ps.close();  
                } 
                catch (IOException e) {  e.printStackTrace(); }  
        }  
            
    }  
	
	public static void sysCmd(String cmd) throws Exception{
		Process p = Runtime.getRuntime().exec(cmd);
		//System.out.println(cmd);
		StreamGobbler errorGobbler  = new StreamGobbler(p.getErrorStream(), "Error");            
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		p.waitFor();
	}
  
}

