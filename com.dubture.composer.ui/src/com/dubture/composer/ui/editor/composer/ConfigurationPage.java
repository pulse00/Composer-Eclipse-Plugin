package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.dubture.composer.ui.editor.ComposerFormPage;

public class ConfigurationPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.ConfigurationPage";

	protected ComposerFormEditor editor;
	
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

		TableWrapLayout layout = new TableWrapLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		
		left = toolkit.createComposite(form.getBody());
		left.setLayout(new TableWrapLayout());
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		new ConfigSection(this, left);
		new ScriptsSection(this, left);
		
		right = toolkit.createComposite(form.getBody());
		right.setLayout(new TableWrapLayout());
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}
}
