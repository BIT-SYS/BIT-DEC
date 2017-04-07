package bit_dec.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bit_dec.actions.DecAction;
import bit_dec.callgraph.FuncParser;
import bit_dec.dissambler.so.AsmAdCode;
import bit_dec.dissambler.so.AsmFuncModel;
import bit_dec.dissambler.so.AsmStructAna;
import bit_dec.dissambler.so.AsmTextSectionStruct;

public class FuncsView extends ViewPart {

	private List list;
	private ArrayList<String> arrayList;
	private ArrayList<AsmFuncModel> funclist;
	private HashMap<String, AsmFuncModel> funcMap;
	
	public FuncsView() {
		// TODO Auto-generated constructor stub
	}

	public void showFunctions(){
		funclist = AsmTextSectionStruct.textSectionModel.getFuncList();
		funcMap = AsmTextSectionStruct.textSectionModel.getFuncMap();
		int funcNum = funclist.size();
		arrayList = new ArrayList<String>();
		list.removeAll();
		//过滤到没用的函数
		String[] filterFuncs = FuncParser.filterFuncs;
		boolean flag = false;
		for(int i=0;i<funcNum;i++){
			String tempString = "";
			flag = false;
			tempString = funclist.get(i).getFuncName();
			for(String fiterFunc:filterFuncs){
				if (tempString.startsWith(fiterFunc)) {
					flag = true;
					break;
				}
			}
			if (flag == false) {
				list.add(tempString);
				arrayList.add(tempString);
			}
		}
		
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int index =list.getSelectionIndex();
				if(index == -1)
					return;
				IWorkbenchPage page = getViewSite().getPage();
				String funcName = list.getItem(index);
				DecAction.decAction(funcName, page);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		list.addSelectionListener(listener);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		list = new List(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
	}

	public Object[] getList(){;
		return arrayList.toArray();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

}
