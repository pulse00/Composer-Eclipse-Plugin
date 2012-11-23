package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.getcomposer.ComposerPackage;

public class ComposerFormEditor extends SharedHeaderFormEditor {
	protected boolean dirty = false;
	protected ComposerPackage composerPackage;
	protected IDocumentProvider documentProvider;

	public ComposerFormEditor() {
		super();
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		documentProvider = new TextFileDocumentProvider();
		try {
			documentProvider.connect(input);
//			p.getDocument(input);
//			documentProvider.
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		
		// eclipse way to get input, unfortunately does not exist for some
		// reasons?
//		File json = ((IFileEditorInput) input).getFile().getFullPath().toFile();
			
		// workaround
//		String composerJsonFilePath =
//				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
//				((IFileEditorInput) input).getFile().getFullPath().toString();
//		
//		composerFile = new File(composerJsonFilePath);
			
		// ok, cool way here we go
		String json = documentProvider.getDocument(input).get();
		
		composerPackage = ComposerPackage.fromJson(json);
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				setDirty(true);
			}
		});
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

	public void doSave(IProgressMonitor monitor) {
		try {
//			if (documentProvider.canSaveDocument(getEditorInput())) {
//				FileWriter fstream = new FileWriter(composerFile);
//				BufferedWriter out = new BufferedWriter(fstream);
//				out.write(composerPackage.toJson());
//				out.close();
				IDocument document = documentProvider.getDocument(getEditorInput());
				documentProvider.aboutToChange(getEditorInput());
				document.set(composerPackage.toJson());
				documentProvider.saveDocument(monitor, getEditorInput(), document, true);
				documentProvider.changed(getEditorInput());

				setDirty(false);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		editorDirtyStateChanged();
	}

	public ComposerPackage getComposerPackge() {
		return composerPackage;
	}
}
