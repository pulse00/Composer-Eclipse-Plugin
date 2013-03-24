package com.dubture.composer.ui.editor.composer.autoload;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.composer.ComposerFormEditor;

public class AutoloadPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.AutoloadPage";

	protected ComposerFormEditor editor;
	private Composite left;
	private Composite right;
	
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

		TableWrapLayout layout = new TableWrapLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		
		left = toolkit.createComposite(form.getBody());
		left.setLayout(new TableWrapLayout());
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		new Psr0Section(this, left);
		
		right = toolkit.createComposite(form.getBody());
		right.setLayout(new TableWrapLayout());
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
	}	
}
