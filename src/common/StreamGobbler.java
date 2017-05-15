package common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.ui.console.MessageConsoleStream;

public class StreamGobbler extends Thread {
	 InputStream is = null;
	 String type = null;
	 File outFile = null;
	 MessageConsoleStream printer = null;
	 public StreamGobbler(InputStream is, String type) {
		 this.is = is;
		 this.type = type;
	 }
	 
	 public StreamGobbler(InputStream is, String type, String outFile) {
		 this.is = is;
		 this.type = type;
		 this.outFile = new File(outFile);
		 if(this.outFile.exists()) this.outFile.delete();
		
	 }

	 public StreamGobbler(InputStream is, String type, MessageConsoleStream printer) {
		 this.is = is;
		 this.type = type;
		 this.printer = printer;
		 if(this.outFile.exists()) this.outFile.delete();
		
	 }
	 
	 public void run() {
		 try {
			 InputStreamReader isr = new InputStreamReader(is);
			 BufferedReader br = new BufferedReader(isr);
			 String line = null;
			 if(this.outFile!=null){
				OutputStream ops = new BufferedOutputStream(new FileOutputStream(this.outFile));   
				while ((line = br.readLine()) != null)
					ops.write((line+"\n").getBytes());
				ops.flush();   
	            ops.close(); 
			 }
			 else if(this.printer!=null){
				 while ((line = br.readLine()) != null)
					printer.println(line);
			 }
			 else
				 while ((line = br.readLine()) != null);
		 } catch (IOException ioe) {
			 ioe.printStackTrace();
		 }
	 }
}