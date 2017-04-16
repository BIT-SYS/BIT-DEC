package app;

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

import action.*;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	//File
	private IWorkbenchAction newAction;
	private ImportAPKAction openAction;
	private IWorkbenchAction iSaveAction;
	private IWorkbenchAction iSaveasAction;
	private IWorkbenchAction iImport;
	private IWorkbenchAction iExport;
	private IWorkbenchAction iCloseAction;
	private IWorkbenchAction iExitAction;
	//Edit
	private IWorkbenchAction iCutAction;
	private IWorkbenchAction iCopyAction;
	private IWorkbenchAction iPasteAction;
	//Decompile
	private CallGraphAction callGraphAction;
	private FunctionDecAction decAction;
	//View
	private IWorkbenchAction PREFERENCES;
	private IWorkbenchAction iResetPers;
	private IWorkbenchAction iShowViewMenu;
	private IWorkbenchAction iOpenPersDiag;
	//Help
	private IWorkbenchAction iHELP_CONTENTS;
	private IWorkbenchAction iHELP_SEARCH;
	//toolBar
	private IWorkbenchAction iRefresh;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
	protected void makeActions(IWorkbenchWindow window) {
    	/**** create actions *******************************************************/
    	//file Menu
    	newAction     = ActionFactory.NEW.create(window);
		openAction 	  = new ImportAPKAction(window);
    	iSaveAction   = ActionFactory.SAVE.create(window);
    	iSaveasAction = ActionFactory.SAVE_AS.create(window);
		iImport       = ActionFactory.IMPORT.create(window);
		iExport       = ActionFactory.EXPORT.create(window);
		iCloseAction  = ActionFactory.CLOSE.create(window);
		iExitAction   = ActionFactory.QUIT.create(window);
		
		//Edit menu
		iCutAction    = ActionFactory.CUT.create(window);
		iCopyAction   = ActionFactory.COPY.create(window);
		iPasteAction  = ActionFactory.PASTE.create(window);
		
		//Decompiling Menu
		callGraphAction = new CallGraphAction(window);
		decAction     = new FunctionDecAction(window);
		
		//View menu
		PREFERENCES   = ActionFactory.PREFERENCES.create(window);
		iResetPers    = ActionFactory.RESET_PERSPECTIVE.create(window);
		iShowViewMenu = ActionFactory.SHOW_VIEW_MENU.create(window);
		iOpenPersDiag = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
		
		//Help menu
		iHELP_CONTENTS = ActionFactory.HELP_CONTENTS.create(window);
		iHELP_SEARCH = ActionFactory.HELP_SEARCH.create(window);
		
		//toolBar
		iRefresh      = ActionFactory.REFRESH.create(window);
		
		newAction.setText("New Project...");
		iImport.setText("Import Project...");
		iExport.setText("Export Project...");
		
    	/*****register actions***********************************************/
		//File menu
    	register(newAction);
    	//register(openAction);
		register(iSaveAction);
		register(iSaveasAction);
		register(iImport);
		register(iExport);
		register(iCloseAction);
		register(iExitAction);
		
		//Edit menu
		register(iCutAction);
		register(iCopyAction);
		register(iPasteAction);
		//Decompiler
		//View menu
		register(PREFERENCES);
		register(iResetPers);
		register(iShowViewMenu);
		register(iOpenPersDiag);
		
		//help menu
		register(iHELP_CONTENTS);
		register(iHELP_SEARCH);
		
		//toolBar
		register(iRefresh);
    }

    @Override
	protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu	  = new MenuManager("&File"     ,"");
    	MenuManager editMenu 	  = new MenuManager("&Edit"     ,"");
    	MenuManager decompileMenu = new MenuManager("&Decompile","");
    	MenuManager viewMenu  	  = new MenuManager("&View"     ,"");
    	MenuManager helpMenu 	  = new MenuManager("&Help"     ,"");
    	menuBar.add(fileMenu);
    	menuBar.add(editMenu);
    	menuBar.add(decompileMenu);
    	menuBar.add(viewMenu);
    	menuBar.add(helpMenu);
    	
    	//File Menu
    	fileMenu.add(newAction);
    	fileMenu.add(openAction);
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
    	
    	//Decompile Menu
    	decompileMenu.add(callGraphAction);
    	decompileMenu.add(decAction);
    	
    	//View Menu
    	viewMenu.add(PREFERENCES);
    	viewMenu.add(iResetPers);
    	viewMenu.add(iShowViewMenu);
    	viewMenu.add(iOpenPersDiag);
    	
    	//Help Menu
    	helpMenu.add(iHELP_CONTENTS);
    	helpMenu.add(iHELP_SEARCH);
    }
    
    @Override
	protected void fillCoolBar(ICoolBarManager coolBar){
		// This will add a new toolbar to the application
		//IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
    	IToolBarManager toolbar = new ToolBarManager(SWT.FLAT| SWT.SHADOW_OUT);
		//coolBar.add(new ToolBarContributionItem(toolbar, "main"));
		coolBar.add(toolbar);
		toolbar.add(callGraphAction);
		toolbar.add(decAction);
		toolbar.add(iRefresh);
    }

    
}
