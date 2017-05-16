package app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import utils.Global;
import view.ConsoleFactory;
public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout rightFolder = layout.createFolder("", IPageLayout.RIGHT, 0.6f, editorArea);
		rightFolder.addView(Global.VIEW_ADVANCEDCODE);
		rightFolder.addView(Global.VIEW_CALLGRAPH);
		rightFolder.addView(Global.VIEW_CGF);
		
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.8f, editorArea);
		
		IFolderLayout FuncFolder  = layout.createFolder("", IPageLayout.RIGHT, 0.8f, editorArea);
		FuncFolder.addView(Global.VIEW_FUNCSVIEW);
		
		IFolderLayout leftFolder = layout.createFolder("", IPageLayout.LEFT,  0.3f, editorArea);
		leftFolder.addView(Global.VIEW_NAVIGATOR);
		
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();	
	}
}
