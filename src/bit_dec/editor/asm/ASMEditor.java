package bit_dec.editor.asm;

import org.eclipse.ui.editors.text.TextEditor;

public class ASMEditor extends TextEditor {

	private ColorManager colorManager;

	public ASMEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
