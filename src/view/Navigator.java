package view;

import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import action.NavigatorActionGroup;




public class Navigator extends ResourceNavigator {
	
	private NavigatorActionGroup navigatorActionGroup;
	private CollapseAllHandler collapseAllHandler;
	public Navigator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void makeActions() {

		//复制工作台的action
		navigatorActionGroup = new NavigatorActionGroup( this );
		setActionGroup(navigatorActionGroup);
		
		IHandlerService service = (IHandlerService) getSite().getService(IHandlerService.class);
		service.activateHandler(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR,
    			new ActionHandler(navigatorActionGroup.toggleLinkingAction));
    	collapseAllHandler = new CollapseAllHandler(this.getViewer());
    	service.activateHandler(CollapseAllHandler.COMMAND_ID, collapseAllHandler);
	}
	
	public NavigatorActionGroup getNavigatorActionGroup(){
		return navigatorActionGroup;
	}

}
