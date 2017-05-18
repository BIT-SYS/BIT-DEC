package view;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

import action.FunctionDecAction;
import core.disassembler.model.AsmFunc;

public class FuncsView extends ViewPart {

	private List list = null;
	
	public FuncsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		list = new List(parent, SWT.BORDER | SWT.H_SCROLL| SWT.V_SCROLL);
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int index =list.getSelectionIndex();
				if(index == -1)
					return;
				IWorkbenchPage page = getViewSite().getPage();
				String funcName = list.getItem(index);
				FunctionDecAction.decAction(funcName);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		list.addSelectionListener(listener);
	}

	public List getList(){
		return list;
	}
	
	public void setList(HashMap<String, AsmFunc> funcMap){
		list.removeAll();
		for(String func:funcMap.keySet()){
			list.add(func);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
