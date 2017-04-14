package view;

import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import action.NavigatorActionGroup;

public class NavigatorView extends ResourceNavigator {
	
	private NavigatorActionGroup navigatorActionGroup;
	private CollapseAllHandler collapseAllHandler;
	public NavigatorView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void makeActions() {

		//copy the actions from workbench
		navigatorActionGroup = new NavigatorActionGroup( this );
		setActionGroup(navigatorActionGroup);
		
		//what does the code do??
		IHandlerService service = (IHandlerService) getSite().getService(IHandlerService.class);
		service.activateHandler(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR,new ActionHandler(navigatorActionGroup.toggleLinkingAction));
    	collapseAllHandler = new CollapseAllHandler(this.getViewer());
    	service.activateHandler(CollapseAllHandler.COMMAND_ID, collapseAllHandler);
	}
	
	public NavigatorActionGroup getNavigatorActionGroup(){
		return navigatorActionGroup;
	}

}
