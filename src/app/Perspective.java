package app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import utils.Constant;
import view.ConsoleFactory;
public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout rightFolder = layout.createFolder("", IPageLayout.RIGHT, 0.6f, editorArea);
		rightFolder.addView(Constant.VIEW_ADVANCEDCODE);
		rightFolder.addView(Constant.VIEW_CALLGRAPH);
		rightFolder.addView(Constant.VIEW_CGF);
		
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.8f, editorArea);
		
		IFolderLayout FuncFolder  = layout.createFolder("", IPageLayout.RIGHT, 0.8f, editorArea);
		FuncFolder.addView(Constant.VIEW_FUNCSVIEW);
		
		IFolderLayout leftFolder = layout.createFolder("", IPageLayout.LEFT,  0.3f, editorArea);
		leftFolder.addView(Constant.VIEW_NAVIGATOR);
		
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();	
	}
}
