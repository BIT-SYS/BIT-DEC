package app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import view.ConsoleFactory;
import sys.Constant;
public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout leftFolder = layout.createFolder("", IPageLayout.LEFT,  0.10f, editorArea);
		IFolderLayout mainfolder = layout.createFolder("", IPageLayout.RIGHT, 0.1f, editorArea);
		IFolderLayout srcfolder  = layout.createFolder("", IPageLayout.RIGHT, 0.1f, editorArea);
		//leftFolder.addPlaceholder("BIT_DEC.myNavigator" + ":*");
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.1f, editorArea);
		leftFolder.addView(Constant.VIEW_NAVIVIEW);
		mainfolder.addView(Constant.VIEW_FUNCSVIEW);
		mainfolder.addView(Constant.VIEW_CALLGRAPHVIEW);
		srcfolder.addView(Constant.VIEW_ADCODEVIEW);
		srcfolder.addView(Constant.VIEW_CGFVIEW);
		
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();	
	}
}
