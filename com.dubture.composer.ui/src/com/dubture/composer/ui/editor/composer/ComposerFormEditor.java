package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.getcomposer.core.ComposerPackage;

import com.dubture.composer.ui.actions.InstallAction;
import com.dubture.composer.ui.actions.InstallDevAction;
import com.dubture.composer.ui.actions.SelfUpdateAction;
import com.dubture.composer.ui.actions.UpdateAction;
import com.dubture.composer.ui.actions.UpdateNoDevAction;

public class ComposerFormEditor extends SharedHeaderFormEditor {
	protected boolean dirty = false;
	protected ComposerPackage composerPackage = null;
	protected IDocumentProvider documentProvider;
	
	private ISharedImages sharedImages = null;
	private IProject project;
	
	private IAction installAction = null;
	private IAction installDevAction = null;
	private IAction updateAction = null;
	private IAction updateNoDevAction = null;
	private IAction selfUpdateAction = null;
	
	protected OverviewPage overviewPage;
	protected DependenciesPage dependenciesPage;
	protected ConfigurationPage configurationPage;
	
	// TODO JsonTextEditor some day...
	protected ComposerTextEditor textEditor = new ComposerTextEditor(); 

	public ComposerFormEditor() {
		super();
		documentProvider = textEditor.getDocumentProvider();
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			documentProvider.connect(input);
			
			// TODO some sort of listener to get notified when the file changes
			//
			// 1) document listener: documentProvider.getDocument(input).addDocumentListener
			// 2) documentProvider.addElementStateListener()
			// 3) Resource Listener
			//
			// see: https://github.com/pulse00/Composer-Eclipse-Plugin/issues/23
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		if (input instanceof IFileEditorInput) {
			project = ((IFileEditorInput)input).getFile().getProject();
		}
			
		// ok, cool way here we go
		String json = documentProvider.getDocument(input).get();
		
		composerPackage = new ComposerPackage(json);
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				System.out.println("Property change: " + e.getPropertyName() + ", oldValue: " + e.getOldValue() + ", newValue: " + e.getNewValue());
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
		
		ToolBarManager manager = (ToolBarManager) header.getToolBarManager();
		contributeToToolbar(manager);
	    manager.update(true);
	    
	}
	
	protected void contributeToToolbar(ToolBarManager manager) {
		// this does not work for some reasons? how to make it working and get rid of the action package?
//		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
//		menuService.populateContributionManager(manager, "toolbar:com.dubture.composer.ui.editor.toolbar");
		
		manager.add(getInstallAction());
		manager.add(getInstallDevAction());
		manager.add(new Separator());
		manager.add(getUpdateNoDevAction());
		manager.add(getUpdateAction());
		manager.add(new Separator());
		manager.add(getSelfUpdateAction());
	}
	
	protected ISharedImages getSharedImages() {
		if (sharedImages == null) {
			getSite().getPage().getWorkbenchWindow().getWorkbench().getSharedImages();
		}
		
		return sharedImages;
	}
	
	protected IAction getInstallAction() {
		if (installAction == null) {
			installAction = new InstallAction(project, getSite());
		}
		
		return installAction;
	}
	
	protected IAction getInstallDevAction() {
		if (installDevAction == null) {
			installDevAction = new InstallDevAction(project, getSite());
		}
		
		return installDevAction;
	}
	
	protected IAction getUpdateAction() {
		if (updateAction == null) {
			updateAction = new UpdateAction(project, getSite());
		}
		
		return updateAction;
	}
	
	protected IAction getUpdateNoDevAction() {
		if (updateNoDevAction == null) {
			updateNoDevAction = new UpdateNoDevAction(project, getSite());
		}
		
		return updateNoDevAction;
	}
	
	protected IAction getSelfUpdateAction() {
		if (selfUpdateAction == null) {
			selfUpdateAction = new SelfUpdateAction(project, getSite());
		}
		
		return selfUpdateAction;
	}
	
	@Override
	protected void createPages() {
		overviewPage = new OverviewPage(this, OverviewPage.ID, "Overview");
		dependenciesPage = new DependenciesPage(this, DependenciesPage.ID, "Dependencies");
		configurationPage = new ConfigurationPage(this, ConfigurationPage.ID, "Configuration");

		super.createPages();
	}

	@Override
	protected void addPages() {
		try {
			addPage(overviewPage);
			addPage(dependenciesPage);
			addPage(configurationPage);

//			addDependencyGraph();
//			setActivePage(DependenciesPage.ID);

			// Aww, can't use jsonedit here :(
			// addPage(new JsonTextEditor(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
//	protected void addDependencyGraphPage() throws PartInitException {
//		addPage(new DependencyGraphPage(this, DependencyGraphPage.ID, "Dependency Graph"));
//	}

	public void doSave(IProgressMonitor monitor) {
		try {
			IDocument document = documentProvider.getDocument(getEditorInput());
			documentProvider.aboutToChange(getEditorInput());
			document.set(composerPackage.toJson());
			documentProvider.saveDocument(monitor, getEditorInput(), document, true);
			documentProvider.changed(getEditorInput());

			setDirty(false);
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
	
	public IProject getProject() {
		return project;
	}

	public ComposerPackage getComposerPackge() {
		return composerPackage;
	}
	
}
