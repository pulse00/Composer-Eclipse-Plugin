/**
 * 
 */
package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
public class DependencyGraphPage extends FormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.DependencyGraphPage";

	private PackageInterface phpPackage;
	protected ComposerEditor editor;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public DependencyGraphPage(ComposerEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		phpPackage = editor.getPHPPackge();
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Dependency Graph");
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
		

		createRequireSection(form, toolkit);
	}

	private void createRequireSection(ScrolledForm form, FormToolkit toolkit) {
		Section general = toolkit.createSection(form.getBody(), 448);
		general.setText("Require");
		general.setDescription("The packages your project requires.");

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
