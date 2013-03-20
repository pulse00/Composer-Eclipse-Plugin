package com.dubture.composer.ui.editor.composer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;

public class BuildpathManagementPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.BuildpathManagementPage";

	protected ComposerFormEditor editor;
	
	private Composite left;
	private Composite right;
	
	private PathSection includes;
	private PathSection excludes;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public BuildpathManagementPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText(getPartName());
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
		
		createHeader(form, toolkit);
		
		left = toolkit.createComposite(form.getBody());
		left.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		
		includes = new PathSection(this, left, true);

		right = toolkit.createComposite(form.getBody());
		right.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		excludes = new PathSection(this, right, false);
	}
	
	private void createHeader(ScrolledForm form, FormToolkit toolkit) {
		Composite banner = toolkit.createComposite(form.getBody());
		banner.setLayout(new TableWrapLayout());
		banner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 2));
		
		Section heading = toolkit.createSection(banner, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		heading.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		heading.setText("Automated Buildpath Management");
		heading.setDescription("Eclipse Composer Plugin can take care of your buildpath. It will scan all paths in your installed packages and add those to the buildpath. Additionally, you can add folders that are also included or excluded in your buildpath.");
		
		Button enabled = toolkit.createButton(heading, "Enable automated Buildpath Management (non-functional ATM)", SWT.CHECK);
		enabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: (De)Activate the composer builder for this project, when the button is triggered
				
			}
		});
		heading.setClient(enabled);
		
		// TODO: Set button checked, based on whether the builder is enabled for this project.
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		
		if (includes != null) {
			includes.save();
		}
		
		if (excludes != null) {
			excludes.save();
		}
	}
}
