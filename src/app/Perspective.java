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
		
		IFolderLayout leftFolder = layout.createFolder("", IPageLayout.LEFT,  0.2f, editorArea);
		IFolderLayout mainfolder = layout.createFolder("", IPageLayout.RIGHT, 0, editorArea);
		//IFolderLayout srcfolder  = layout.createFolder("", IPageLayout.RIGHT, 0.5f, editorArea);
		//leftFolder.addPlaceholder(Constant.VIEW_NAVIGATOR + ":*");
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.8f, editorArea);
		leftFolder.addView(Constant.VIEW_NAVIGATOR);
		//leftFolder.addView(Constant.NAVIGATOR);
		mainfolder.addView(Constant.VIEW_FUNCSVIEW);
		mainfolder.addView(Constant.VIEW_CALLGRAPH);
		mainfolder.addView(Constant.VIEW_ADVANCEDCODE);
		mainfolder.addView(Constant.VIEW_CGF);
		mainfolder.addView("bit_dec.ASMeditors.ASMEditor");
		//srcfolder.addView(Constant.VIEW_ADVANCEDCODE);
		//srcfolder.addView(Constant.VIEW_CGF);
		
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();	
	}
}
