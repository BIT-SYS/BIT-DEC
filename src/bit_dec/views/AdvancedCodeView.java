package bit_dec.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class AdvancedCodeView extends ViewPart {

	private Text text;
	public AdvancedCodeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.BORDER|SWT.WRAP|SWT.MULTI);
		
	}

	public void showConten(String temp,int step){
		for(int i=0;i<step;i++){
			text.append(" ");
		}
		text.append(temp);
	}
	public void init(){
		text.setText("");
	}
	@Override
	public void setFocus() {
		 text.setFocus();
	}

}
