package com.dubture.composer.ui.editor;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.dubture.composer.ui.editor.composer.ComposerFormEditor;

public class ComposerFormPage extends FormPage {

	protected boolean enabled = true;
	
	public ComposerFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public ComposerFormEditor getComposerEditor() {
		return (ComposerFormEditor) getEditor();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void contributeToToolbar(IToolBarManager manager, IManagedForm headerForm) {
		
	}
}
