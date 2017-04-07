package bit_dec;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import bit_dec.views.ConsoleFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout leftFolder = layout.createFolder("leftFolderLayout",
				IPageLayout.LEFT, 0.10f, editorArea);
		leftFolder.addPlaceholder("BIT_DEC.myNavigator" + ":*");
		leftFolder.addView("BIT_DEC.myNavigator");
		
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,
				.8f, editorArea);
		ConsoleFactory consoleFactory = new ConsoleFactory();
		consoleFactory.openConsole();
		
		
		IFolderLayout mainfolder = layout.createFolder("mainfolder",
				IPageLayout.RIGHT, 0.75f, editorArea);
		mainfolder.addView("BIT_DEC.functionsView");
		mainfolder.addView("BIT_DEC.callgraphView");
		
		IFolderLayout srcfolder = layout.createFolder("srcfolder",
				IPageLayout.RIGHT, 0.45f, editorArea);
		srcfolder.addView("BIT_DEC.advanced_code");
		srcfolder.addView("BIT_DEC.cfgView");
		
		
		
		
		//layout.addView("BIT_DEC.functionsView", IPageLayout.RIGHT, .8f, editorArea);
		//layout.addView("BIT_DEC.advanced_code", IPageLayout.RIGHT, .7f, editorArea);
		
		

	}
}
