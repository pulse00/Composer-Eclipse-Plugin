package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
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
import org.sourceforge.jsonedit.core.editors.JsonTextEditor;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.actions.InstallAction;
import com.dubture.composer.ui.actions.InstallDevAction;
import com.dubture.composer.ui.actions.SelfUpdateAction;
import com.dubture.composer.ui.actions.UpdateAction;
import com.dubture.composer.ui.actions.UpdateNoDevAction;
import com.dubture.composer.ui.editor.toolbar.SearchControl;
import com.dubture.getcomposer.core.ComposerPackage;

public class ComposerFormEditor extends SharedHeaderFormEditor implements IDocumentListener, IResourceChangeListener {
	
	public static final String ID = "com.dubture.composer.ui.editor.composer.ComposerEditor";
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
	
	private int jsonEditorIndex;
	private int lastPageIndex = -1;
	
	protected OverviewPage overviewPage;
	protected DependenciesPage dependenciesPage;
	protected ConfigurationPage configurationPage;
	protected AutoloadPage autoloadPage;
	protected JsonTextEditor jsonEditor;

	private String jsonDump;
	private boolean saving = false;
	private boolean pageChanging = false;
	private DependencyGraphPage graphPage;
	
	private IFile jsonFile;
	private SearchControl searchControl;

	public ComposerFormEditor() {
		super();
		jsonEditor = new JsonTextEditor();
		documentProvider = jsonEditor.getDocumentProvider();
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			documentProvider.connect(input);
			documentProvider.getDocument(getEditorInput()).addDocumentListener(this);
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		if (input instanceof IFileEditorInput) {
			jsonFile = ((IFileEditorInput)input).getFile();
			if (jsonFile != null) {
				project = jsonFile.getProject();
				setPartName(project.getName());
				ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
			}
		}
			
		// ok, cool way here we go
		String json = documentProvider.getDocument(input).get();
		
		composerPackage = new ComposerPackage(json);
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Logger.debug("Property change: " + e.getPropertyName() + ", oldValue: " + e.getOldValue() + ", newValue: " + e.getNewValue());
				setDirty(true);
			}
		});
	}

	@Override
	protected void createPages() {
		overviewPage = new OverviewPage(this, OverviewPage.ID, "Overview");
		dependenciesPage = new DependenciesPage(this, DependenciesPage.ID, "Dependencies");
		configurationPage = new ConfigurationPage(this, ConfigurationPage.ID, "Configuration");
		autoloadPage = new AutoloadPage(this, AutoloadPage.ID, "Autoload");
		graphPage = new DependencyGraphPage(this, DependencyGraphPage.ID, "Dependency Graph", searchControl);

		super.createPages();
	}

	@Override
	protected void addPages() {
		try {
			addPage(overviewPage);
			addPage(dependenciesPage);
			addPage(autoloadPage);
			addPage(configurationPage);
			addPage(graphPage);
			jsonEditorIndex = addPage(jsonEditor, getEditorInput());
			setPageText(jsonEditorIndex, jsonEditor.getTitle());
		} catch (PartInitException e) {
			Logger.logException(e);
		}
	}
	
	@Override
	protected void pageChange(int newPageIndex) {
		// change page first
		super.pageChange(newPageIndex);
		
		pageChanging = true;
		ToolBarManager manager = (ToolBarManager) getHeaderForm().getForm().getToolBarManager();
		IContributionItem toggleDevAction = manager.find("toggleDev");
		toggleDevAction.setVisible(newPageIndex == 4);
		searchControl.setVisible(newPageIndex == 4);
		manager.update(true);
		
		// react to it
		if (getActiveEditor() == jsonEditor) {
			IDocument document = documentProvider.getDocument(getEditorInput());
			jsonDump = composerPackage.toJson();
			document.set(jsonDump);
			
			getHeaderForm().getForm().setText(jsonEditor.getTitle());
		}
		
		if (lastPageIndex != -1 && lastPageIndex == jsonEditorIndex) {
			String json = documentProvider.getDocument(jsonEditor.getEditorInput()).get();
			if (jsonDump != null && !jsonDump.equals(json)) {
				composerPackage.fromJson(json);
			}
		}
		
		lastPageIndex = newPageIndex;
		pageChanging = false;
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		ScrolledForm header = headerForm.getForm();
		header.setText("Composer");
		
		FormToolkit toolkit = headerForm.getToolkit();
		toolkit.decorateFormHeading(header.getForm());
		
		ToolBarManager manager = (ToolBarManager) header.getToolBarManager();
		
		contributeToToolbar(manager, headerForm);
	    manager.update(true);
	    
	}
	
	
	protected void contributeToToolbar(ToolBarManager manager, IManagedForm headerForm) {
		// this does not work for some reasons? how to make it working and get rid of the action package?
//		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
//		menuService.populateContributionManager(manager, "toolbar:com.dubture.composer.ui.editor.toolbar");

		searchControl = new SearchControl("composer.SearchControl", headerForm);
		
		manager.add(searchControl);
		manager.add(new ToggleDevAction());
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
	
	public void doSave(IProgressMonitor monitor) {
		try {
			saving = true;
			IDocument document = documentProvider.getDocument(getEditorInput());
			
			// load from json editor when currently active
			if (getActivePage() == jsonEditorIndex) {
				String json = document.get();
				if (jsonDump != null && !jsonDump.equals(json)) {
					composerPackage.fromJson(json);
				}
				jsonDump = json;
			}
			
			// write
			documentProvider.aboutToChange(getEditorInput());
			document.set(composerPackage.toJson());
			documentProvider.saveDocument(monitor, getEditorInput(), document, true);
			documentProvider.changed(getEditorInput());

			setDirty(false);
			saving = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {

	}

	@Override
	public void documentChanged(DocumentEvent event) {
		// changes happen outside eclipse
		if (!pageChanging && !saving) {
			String contents = event.getDocument().get();
			if (getActiveEditor() == jsonEditor) { 
				IDocument document = documentProvider.getDocument(getEditorInput());
				if (document.get() != null && document.get().equals(contents) == false) {
					document.set(contents);
				}
			} else {
				composerPackage.fromJson(contents);
			}
			setDirty(false);
		}
		
		// changes in eclipse
		if (!saving && jsonDump != null && !jsonDump.equals(event.getDocument().get())) {
			setDirty(true);
		}
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


	/**
	 * Based on org.eclipse.m2e.editor.pom.MavenPomEditor
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		if (jsonFile == null) {
			return;
		}

	    //handle project delete
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE || event.getType() == IResourceChangeEvent.PRE_DELETE) {
			if (jsonFile.getProject().equals(event.getResource())) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						close(false);
					}
				});
			}
			return;
		}

		// handle json delete
		class RemovedResourceDeltaVisitor implements IResourceDeltaVisitor {
			boolean removed = false;
			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta.getResource() != null && delta.getResource().equals(jsonFile) && (delta.getKind() & (IResourceDelta.REMOVED)) != 0) {
					removed = true;
					return false;
				}
				return true;
			}
		}
		;

		try {
			RemovedResourceDeltaVisitor visitor = new RemovedResourceDeltaVisitor();
			event.getDelta().accept(visitor);
			if (visitor.removed) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						close(true);
					}
				});
			}
		} catch (CoreException ex) {
			Logger.logException(ex);
		}
	}
	
	protected class ToggleDevAction extends Action {

		private boolean showDev;

		public ToggleDevAction() {
			super("Toggle dev packages");
			setDescription("Toggle dev packages");
			setToolTipText("Toggle dev packages");
			setId("toggleDev");
			DLTKPluginImages.setLocalImageDescriptors(this, "th_showqualified.gif"); //$NON-NLS-1$
		}

		public void run() {
			showDev = !showDev;
			graphPage.applyFilter(showDev);
		}
	}
}
