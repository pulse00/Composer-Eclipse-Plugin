package com.dubture.composer.ui.editor.composer;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class ComposerTextEditor extends TextEditor {

	public ComposerTextEditor() {
		super();
		setDocumentProvider(new TextFileDocumentProvider());
	}
}
