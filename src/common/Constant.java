//��д�ߣ�������
//2017��4��11��
package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.ui.console.MessageConsoleStream;

import view.ConsoleFactory;

public class Constant {
	public static final String PLUGIN_ID 		 = "bit-dec";
	public static final String PERSPECTIVE_ID    = "app.Perspective";
	public static final String VIEW_ADVANCEDCODE = "view.AdvancedCodeView";
	public static final String VIEW_CALLGRAPH    = "view.CallGraphView";
	public static final String VIEW_CGF          = "view.CFGView";
	public static final String VIEW_FUNCSVIEW    = "view.FuncsView";
	public static final String VIEW_NAVIGATOR    = "view.NavigatorView";
	
	public static String TEMPLATE     = "D:/WorkSpace/eclipse-rcp/bit-dec/resources/template";
	public static String OUTPUTFOLDER = "D:/WorkSpace/eclipse-rcp/bit-dec/resources/";
	public static String PROJECTPATH = "";
	public static MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
	
	public static void unzipFile(String filePath, String projectPath) throws Exception{
//		this.apk2smali(this.filePath);
//		this.smali2java(this.projectPath+"/smali");
//		FileTools.deleteFile(new File(this.projectPath+"/smali"));
				
		Constant.printer.println(filePath+" is being under decompression...");
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
        Constant.printer.println("decompression used time： "+(endTime-startTime)+" ms");  
 
		//ZipUtils.decompress(this.filePath, this.projectPath);
		Constant.printer.println(filePath+" decompressed completly.");
	}
	

}

