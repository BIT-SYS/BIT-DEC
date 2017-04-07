package bit_dec;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import bit_dec.views.ConsoleFactory;

public class debugPerspective implements IPerspectiveFactory {
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout leftFolder = layout.createFolder("leftFolderLayout", IPageLayout.LEFT, 0.13f, editorArea);
		leftFolder.addPlaceholder("BIT_DEC.myNavigator"+":*"); 
		leftFolder.addView("BIT_DEC.myNavigator");
        
        layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, .8f, editorArea);
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();
		   
		IFolderLayout mainfolder = layout.createFolder("mainfolder", IPageLayout.LEFT, 0.80f, editorArea);//新建一个Folder 
		mainfolder.addView("BIT_DEC.callgraphView");//resource navigator
		mainfolder.addView("BIT_DEC.cfgView");//resource navigator 
		
		layout.addView("BIT_DEC.functionsView", IPageLayout.RIGHT, .8f, editorArea);
		
		
	}

}
