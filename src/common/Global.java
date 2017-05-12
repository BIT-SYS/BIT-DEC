package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
	public static String PROGRAMLOCATION = "D:\\WorkSpace\\eclipse-rcp\\BIT-DEC";
	public static String OUTPUTFOLDER    = PROGRAMLOCATION + "\\resources\\";
	public static String TEMPLATE        = PROGRAMLOCATION + "\\resources\\template";
	
	public static HashMap<String, String> APKPATH = new HashMap<String, String>();
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
	

}

