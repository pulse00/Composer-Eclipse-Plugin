package com.dubture.composer.core.ui.editor.composer;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;

public class ComposerEditor extends SharedHeaderFormEditor {
	protected boolean dirty = false;
	protected PackageInterface phpPackage;

	public ComposerEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		try {
			File json = ((IFileEditorInput) input).getFile().getFullPath()
					.toFile();
			phpPackage = PHPPackage.fromJson(json);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		ScrolledForm header = headerForm.getForm();
		header.setText("Composer");

		FormToolkit toolkit = headerForm.getToolkit();
		toolkit.decorateFormHeading(header.getForm());
	}

	@Override
	protected void addPages() {
		try {
			addPage(new OverviewPage(this, OverviewPage.ID, "Overview"));
			addPage(new DependenciesPage(this, DependenciesPage.ID, "Dependencies"));
			
			// Aww, can't use jsonedit here :(
			//addPage(new JsonTextEditor(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub
		setDirty(false);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean value) {
		this.dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	public PackageInterface getPHPPackge() {
		return phpPackage;
	}
}
