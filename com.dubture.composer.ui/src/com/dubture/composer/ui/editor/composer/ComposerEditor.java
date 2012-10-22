package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;

public class ComposerEditor extends SharedHeaderFormEditor {
	protected boolean dirty = false;
	protected PHPPackage phpPackage;

	public ComposerEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		try {
			
			// eclipse way to get input, unfortunately does not exist for some
			// reasons?
//			File json = ((IFileEditorInput) input).getFile().getFullPath().toFile();
			
			// workaround
			String composerJsonFilePath =
					ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
					((IFileEditorInput) input).getFile().getFullPath().toString();
			
			File json = new File(composerJsonFilePath);
			
			phpPackage = PHPPackage.fromJson(json);
			phpPackage.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent arg0) {
					setDirty(true);
				}
			});
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
			addOverview();
			addDependencies();
			addDependencyGraph();

			// Aww, can't use jsonedit here :(
			// addPage(new JsonTextEditor(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void addOverview() throws PartInitException {
		addPage(new OverviewPage(this, OverviewPage.ID, "Overview"));
	}

	protected void addDependencies() throws PartInitException {
		addPage(new DependenciesPage(this, DependenciesPage.ID, "Dependencies"));
	}
	
	protected void addDependencyGraph() throws PartInitException {
		addPage(new DependencyGraphPage(this, DependencyGraphPage.ID, "Dependency Graph"));
	}

	public void doSave(IProgressMonitor arg0) {
		System.out.println(phpPackage.toJson());
		setDirty(false);
	}

	public void doSaveAs() {
	}

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
