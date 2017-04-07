package bit_dec;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import bit_dec.actions.*;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction NewAction;
	private IWorkbenchAction iExitAction;
	private IWorkbenchAction iSaveAction;
	private IWorkbenchAction iSaveasAction;
	private IWorkbenchAction iCloseAction;
	private IWorkbenchAction iCutAction;
	private IWorkbenchAction iCopyAction;
	private IWorkbenchAction iPasteAction;
	private IWorkbenchAction PREFERENCES;
	private IWorkbenchAction iRefresh;
	private IWorkbenchAction iResetPers;
	private IWorkbenchAction iShowViewMenu;
	private IWorkbenchAction iOpenPersDiag;
	private IWorkbenchAction iHELP_CONTENTS;
	private IWorkbenchAction iHELP_SEARCH;
	private IWorkbenchAction iImport;
	private IWorkbenchAction iExport;
	
	
	private OpenAction OpenAction;
	private CallGraphAction callGraphAction;
	private DecAction decAction;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
	protected void makeActions(IWorkbenchWindow window) {
    	//file Menu
    	NewAction     = ActionFactory.NEW.create(window);
    	
    	iSaveAction   = ActionFactory.SAVE.create(window);
    	iSaveasAction = ActionFactory.SAVE_AS.create(window);
		iCloseAction  = ActionFactory.CLOSE.create(window);
		
		iImport       = ActionFactory.IMPORT.create(window);
		iExport       = ActionFactory.EXPORT.create(window);
		iExitAction   = ActionFactory.QUIT.create(window);
		
		//Edit menu
		iCutAction    = ActionFactory.CUT.create(window);
		iCopyAction   = ActionFactory.COPY.create(window);
		iPasteAction  = ActionFactory.PASTE.create(window);
		
		//Decompiling Menu
		callGraphAction = new CallGraphAction(window);
		decAction = new DecAction(window);
		
		//View menu
		PREFERENCES   = ActionFactory.PREFERENCES.create(window);
		iRefresh      = ActionFactory.REFRESH.create(window);
		iResetPers    = ActionFactory.RESET_PERSPECTIVE.create(window);
		iShowViewMenu = ActionFactory.SHOW_VIEW_MENU.create(window);
		iOpenPersDiag = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
		
		//Help menu
		iHELP_CONTENTS = ActionFactory.HELP_CONTENTS.create(window);
		iHELP_SEARCH = ActionFactory.HELP_SEARCH.create(window);
		
		OpenAction = new OpenAction(window);
		
		NewAction.setText("New Project...");
		iImport.setText("Import Project...");
		iExport.setText("Export Project...");
    	
    	register(NewAction);
		register(iExitAction);
		register(iSaveAction);
		register(iSaveasAction);
		register(iCloseAction);
		register(iImport);
		register(iExport);
		
		//Edit Menu
		register(iCutAction);
		register(iCopyAction);
		register(iPasteAction);
		
		
		
		//others
		register(PREFERENCES);
		register(iRefresh);
		register(iResetPers);
		register(iShowViewMenu);
		register(iOpenPersDiag);
		
		//help
		register(iHELP_CONTENTS);
		register(iHELP_SEARCH);
    }

    @Override
	protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager("&File","");
    	MenuManager editMenu = new MenuManager("&Edit","");
    	MenuManager OperateMenu = new MenuManager("&Decompile","");
    	MenuManager OthersMenu = new MenuManager("&View","");
    	MenuManager HelpMenu = new MenuManager("&Help","");
    	menuBar.add(fileMenu);
    	menuBar.add(editMenu);
    	menuBar.add(OperateMenu);
    	menuBar.add(OthersMenu);
    	menuBar.add(HelpMenu);
    	
    	//file Menu
    	fileMenu.add(NewAction);
    	fileMenu.add(OpenAction);
    	fileMenu.add(iSaveAction);
    	fileMenu.add(iSaveasAction);
    	fileMenu.add(new Separator());
    	fileMenu.add(iImport);
    	fileMenu.add(iExport);
    	fileMenu.add(new Separator());
    	fileMenu.add(iCloseAction);
    	fileMenu.add(iExitAction);
    	
    	//Edit Menu
    	editMenu.add(iCutAction);
    	editMenu.add(iCopyAction);
    	editMenu.add(iPasteAction);
    	
    	//Operate Menu
    	OperateMenu.add(new Separator());
    	OperateMenu.add(new Separator());
    	OperateMenu.add(callGraphAction);
    	OperateMenu.add(decAction);
    	
    	//Others Menu
    	OthersMenu.add(PREFERENCES);
    	OthersMenu.add(iResetPers);
    	OthersMenu.add(iShowViewMenu);
    	OthersMenu.add(iOpenPersDiag);
    	
    	//Help Menu
    	HelpMenu.add(iHELP_CONTENTS);
    	HelpMenu.add(iHELP_SEARCH);
    }
    
    @Override
	protected void fillCoolBar(ICoolBarManager coolBar){
		// This will add a new toolbar to the application
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main"));
		toolbar.add(callGraphAction);
		toolbar.add(decAction);
		toolbar.add(iRefresh);
    }

    
}
