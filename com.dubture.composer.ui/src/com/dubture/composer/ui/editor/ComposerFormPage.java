package com.dubture.composer.ui.editor;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.dubture.composer.ui.editor.composer.ComposerFormEditor;

public abstract class ComposerFormPage extends FormPage {

	public ComposerFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public ComposerFormEditor getComposerEditor() {
		return (ComposerFormEditor) getEditor();
	}
}
