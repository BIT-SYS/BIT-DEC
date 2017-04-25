package test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class browser2 {
	public static void main(String args[]){
	   Display display = new Display();
	   final Shell shell = new Shell(display, SWT.SHELL_TRIM);
	   shell.setLayout(new GridLayout());
	   Browser browser = new Browser(shell, SWT.NONE);
	   browser.addTitleListener(new TitleListener() {
		@Override
		public void changed(TitleEvent event) {
			// TODO Auto-generated method stub
			
		}
	   });
	   browser.setBounds(0,0,600,400);
	   shell.pack();
	   shell.open();
	   browser.setUrl("file://d:/tmp/force1.html");
	   while (!shell.isDisposed())
	      if (!display.readAndDispatch())
	         display.sleep();
	}
}
