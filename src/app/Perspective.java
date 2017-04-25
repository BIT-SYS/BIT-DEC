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
		
		IFolderLayout rightFolder = layout.createFolder("", IPageLayout.RIGHT, 0.5f, editorArea);
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.8f, editorArea);
		IFolderLayout leftFolder = layout.createFolder("", IPageLayout.LEFT,  0.3f, editorArea);
		rightFolder.addView(Constant.VIEW_FUNCSVIEW);
		rightFolder.addView(Constant.VIEW_CALLGRAPH);
		rightFolder.addView(Constant.VIEW_ADVANCEDCODE);
		rightFolder.addView(Constant.VIEW_CGF);
		//rightFolder.addView("bit_dec.ASMeditors.ASMEditor");
		leftFolder.addView(Constant.VIEW_NAVIGATOR);
		//srcfolder.addView(Constant.VIEW_ADVANCEDCODE);
		//srcfolder.addView(Constant.VIEW_CGF);
		
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();	
	}
}
