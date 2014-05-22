package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;

public class AutoloadPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.AutoloadPage";

	protected ComposerFormEditor editor;
	private Composite left;
	private Composite right;
	
	private Psr0Section psr0Section;
	private Psr4Section psr4Section;	
	
	public AutoloadPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Autoloading");
		}
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		form.getBody().setLayout(FormLayoutFactory.createFormGridLayout(true, 2));
		
		left = toolkit.createComposite(form.getBody(), SWT.NONE);
		left.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		left.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		psr4Section = new Psr4Section(this, left);
		psr4Section.setEnabled(enabled);
		
		psr0Section = new Psr0Section(this, left);
		psr0Section.setEnabled(enabled);
		
		right = toolkit.createComposite(form.getBody(), SWT.NONE);
		right.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}	
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (psr0Section != null) {
			psr0Section.setEnabled(enabled);
		}
		
		if (psr4Section != null) {
			psr4Section.setEnabled(enabled);
		}
	}
}
