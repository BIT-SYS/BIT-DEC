package utils;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import view.NavigatorView;
import app.Activator;
import sys.Constant;
public class PathTools {
	
	private static IStructuredSelection _selection;
	private static IWorkbenchWindow _window;
	
	/**
	 * �@���x���Ŀ��·��
	 * @param window
	 * @return
	 */
	public static String getProjectPath(IWorkbenchWindow window) {
		_window = window;
		init();
		try{
			IProject iproject = (IProject) _selection.getFirstElement();
			return iproject.getLocation().toString();
		}catch (NullPointerException e) {
			MessageDialog.openInformation(window.getShell(), "����", "���ȵ����Ҫ��������Ŀ����"); 
			return null;
		}
	}

	/**
	 * �@���x���ļ���·��
	 * @param window
	 * @return
	 */
	public static String getFilePath(IWorkbenchWindow window) {
		_window = window;
		init();
		if(_selection.getFirstElement() instanceof IFile) {
			IFile file = (IFile) _selection.getFirstElement();
			return file.getLocation().toString();
		}
		return null;
	}
	/**
	 * ���߰�·��
	 * @return
	 */
	public static String getToolsPath(){
		URL url = Activator.getDefault().getBundle().getResource("tools");
		try {
			return FileLocator.toFileURL(url).toString().substring(6);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ��ȡsmali·��
	 * @param window
	 * @return
	 */
	public static String getSmaliPath(IWorkbenchWindow window){
		String projectPath = getFilePath(window);
		return projectPath+"/smali1/smali";
	}

	private static void init(){
		IWorkbenchPage page = _window.getActivePage();
		NavigatorView viewPart = (NavigatorView)page.findView(Constant.VIEW_NAVIGATOR);
		ISelectionService service = viewPart.getNavigatorActionGroup().getNavigator().getSite().getWorkbenchWindow()
				.getSelectionService();
		_selection = (IStructuredSelection) service.getSelection(Constant.VIEW_NAVIGATOR);
		
	}
	
	
}
