/**
 * 
 */
package com.dubture.composer.core.ui.editor.composer;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.getcomposer.core.PackageInterface;

/**
 * @author Thomas Gossmann
 * 
 */
public class OverviewPage extends FormPage {

	public final static String ID = "com.dubture.composer.core.ui.editor.composer.OverviewPage";

	private PackageInterface phpPackage;
	protected ComposerEditor editor;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public OverviewPage(ComposerEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		phpPackage = editor.getPHPPackge();
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Overview");
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		GridLayout layout = new GridLayout();
//		layout.marginWidth = 10;
//		layout.marginHeight = 10;
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		

		createGeneralSection(form, toolkit);
	}

	private void createGeneralSection(ScrolledForm form, FormToolkit toolkit) {
		Section general = toolkit.createSection(form.getBody(), 448);
		general.setText("General Information");
		general.setDescription("This section describes general information about your Package");

		Composite client = toolkit.createComposite(general, 64);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

//		Label unixnameLbl = 
				toolkit.createLabel(client, "Name (vendor/project):");
//		GridData gd = new GridData();
//		gd.widthHint = 75;
//		unixnameLbl.setLayoutData(gd);
				
		toolkit.createText(client, "");


//		general.setClient(client);
	}

}
