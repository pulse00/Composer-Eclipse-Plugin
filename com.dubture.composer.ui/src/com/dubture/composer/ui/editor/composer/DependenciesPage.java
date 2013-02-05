/**
 * 
 */
package com.dubture.composer.ui.editor.composer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.getcomposer.ComposerPackage;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;

/**
 * @author Thomas Gossmann
 * 
 */
public class DependenciesPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.DependencyPage";

	private ComposerPackage composerPackage;
	protected ComposerFormEditor editor;

	protected Composite left;
	protected Composite right;

	protected DependencySection requireSection;
	protected TableViewer requireView;
	protected Button requireEdit;
	protected Button requireRemove;
	
	protected DependencySection requireDevSection;
	protected TableViewer requireDevView;
	protected Button requireDevEdit;
	protected Button requireDevRemove;
	
	
	
	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public DependenciesPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		composerPackage = editor.getComposerPackge();
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Dependencies");
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
		
		requireSection = new DependencySection(this, left, composerPackage.getRequire(), "Require", "The dependencies for your package.", true);
		requireDevSection = new DependencySection(this, left, composerPackage.getRequireDev(), "Require (Development)", "The development dependencies for your package.", false);
		
		requireSection.getSection().addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				requireDevSection.getSection().setExpanded(!e.getState());
				((GridData)requireSection.getSection().getLayoutData()).grabExcessVerticalSpace = e.getState();
				((GridData)requireDevSection.getSection().getLayoutData()).grabExcessVerticalSpace = !e.getState();
			}
			
			public void expansionStateChanged(ExpansionEvent e) {
			}
		});
		
		requireDevSection.getSection().addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				requireSection.getSection().setExpanded(!e.getState());
				((GridData)requireDevSection.getSection().getLayoutData()).grabExcessVerticalSpace = e.getState();
				((GridData)requireSection.getSection().getLayoutData()).grabExcessVerticalSpace = !e.getState();
			}
			
			public void expansionStateChanged(ExpansionEvent e) {
			}
		});

		right = toolkit.createComposite(form.getBody(), SWT.NONE);
		right.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		DependencySearchSection searchSection = new DependencySearchSection(this, right);
		
		
	}
}
