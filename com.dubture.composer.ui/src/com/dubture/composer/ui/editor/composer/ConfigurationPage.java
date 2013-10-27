package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;

public class ConfigurationPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.ConfigurationPage";

	protected ComposerFormEditor editor;
	
	protected ConfigSection configSection;
	protected ScriptsSection scriptsSection;
	protected RepositoriesSection repositoriesSection;
	
	private Composite left;
	private Composite right;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public ConfigurationPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Configuration");
		}
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		// general config settings (bin & target-dir)
		// config
		// packages
		// scripts
		
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		form.getBody().setLayout(FormLayoutFactory.createFormGridLayout(true, 2));
		
		left = toolkit.createComposite(form.getBody(), SWT.NONE);
		left.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		left.setLayoutData(new GridData(GridData.FILL_BOTH));

		configSection = new ConfigSection(this, left);
		configSection.setEnabled(enabled);
		
		scriptsSection = new ScriptsSection(this, left);
		scriptsSection.setEnabled(enabled);
		
		right = toolkit.createComposite(form.getBody(), SWT.NONE);
		right.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		repositoriesSection = new RepositoriesSection(this, right);
		repositoriesSection.setEnabled(enabled);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (configSection != null) {
			configSection.setEnabled(enabled);
			scriptsSection.setEnabled(enabled);
			repositoriesSection.setEnabled(enabled);
		}
	}
}
