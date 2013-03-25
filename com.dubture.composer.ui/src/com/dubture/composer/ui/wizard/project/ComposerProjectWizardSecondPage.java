package com.dubture.composer.ui.wizard.project;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.WordUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.dltk.internal.ui.wizards.BuildpathDetector;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.dltk.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.language.LanguageModelInitializer;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.wizards.IPHPProjectCreateWizardPage;
import org.eclipse.php.internal.ui.wizards.PHPBuildpathDetector;
import org.eclipse.php.internal.ui.wizards.PHPProjectWizardFirstPage;
import org.eclipse.php.ui.util.PHPProjectUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.getcomposer.core.objects.Namespace;

@SuppressWarnings("restriction")
public class ComposerProjectWizardSecondPage extends CapabilityConfigurationPage implements IPHPProjectCreateWizardPage, Observer {

	protected static final String FILENAME_BUILDPATH = ".buildpath"; //$NON-NLS-1$
	protected final ComposerProjectWizardFirstPage firstPage;
	protected URI currentProjectLocation; // null if location is platform location
	protected AutoloadGroup autoloadGroup;
	private Boolean fIsAutobuild;
	private AutoloadValidator validator;

	public ComposerProjectWizardSecondPage(ComposerProjectWizardFirstPage mainPage) {
		super("Dependencies");
		setPageComplete(false);
		setTitle("Autoloading settings");
		setDescription("Setup autoloading for your project.");
		firstPage = mainPage;
		currentProjectLocation = null;
		fIsAutobuild = null;
	}

	@Override
	public void createControl(Composite parent) {

		int numColumns = 1;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(numColumns, false));
		
		final Group group = new Group(composite, SWT.None);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(numColumns, false));
		group.setText("PSR-0");
		
		autoloadGroup = new AutoloadGroup(group, getShell());
		autoloadGroup.addObserver(this);
		
		validator = new AutoloadValidator(this);
		autoloadGroup.addObserver(validator);
		
		Dialog.applyDialogFont(composite);
		setHelpContext(composite);
		setControl(composite);
		
		firstPage.settingsGroup.addObserver(this);		

	}

	@Override
	public void initPage() {
		changeToNewProject();
	}

	@Override
	protected String getScriptNature() {
		return PHPNature.ID;
	}

	private void changeToNewProject() {
		firstPage.getDetect();

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (fIsAutobuild == null) {
						fIsAutobuild = Boolean.valueOf(CoreUtility.enableAutoBuild(false));
					}
					updateProject(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			}
		};

		try {
			getContainer().run(true, false, new WorkspaceModifyDelegatingOperation(op));
		} catch (InvocationTargetException e) {
			final String title = NewWizardMessages.ScriptProjectWizardSecondPage_error_title;
			final String message = NewWizardMessages.ScriptProjectWizardSecondPage_error_message;
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch (InterruptedException e) {
			// cancel pressed
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void updateProject(IProgressMonitor monitor) throws CoreException, InterruptedException {

		IProject projectHandle = firstPage.getProjectHandle();
		IScriptProject create = DLTKCore.create(projectHandle);
		super.init(create, null, false);
		currentProjectLocation = getProjectLocationURI();

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(NewWizardMessages.ScriptProjectWizardSecondPage_operation_initialize, 70);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			createProject(getProject(), currentProjectLocation, new SubProgressMonitor(monitor, 20));

			IBuildpathEntry[] buildpathEntries = null;
			
			//TODO: see https://github.com/pulse00/Composer-Eclipse-Plugin/issues/37
			IPath srcPath = new Path("src");

			if (srcPath.segmentCount() > 0) {
				IFolder folder = getProject().getFolder(srcPath);
				CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 10));
			} else {
				monitor.worked(10);
			}

			final IPath projectPath = getProject().getFullPath();

			// configure the buildpath entries, including the default
			// InterpreterEnvironment library.
			List cpEntries = new ArrayList();
			cpEntries.add(DLTKCore.newSourceEntry(projectPath.append(srcPath)));
			cpEntries.add(DLTKCore.newContainerEntry(LanguageModelInitializer.LANGUAGE_CONTAINER_PATH));

			buildpathEntries = (IBuildpathEntry[]) cpEntries.toArray(new IBuildpathEntry[cpEntries.size()]);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			init(DLTKCore.create(getProject()), buildpathEntries, false);
			setPhpLangOptions();
			configureScriptProject(new SubProgressMonitor(monitor, 30));

			// adding build paths, and language-Container:
			getScriptProject().setRawBuildpath(buildpathEntries, new NullProgressMonitor());
			LanguageModelInitializer.enableLanguageModelFor(getScriptProject());
		} finally {
			monitor.done();
		}
	}

	protected URI getProjectLocationURI() throws CoreException {
		if (firstPage.isInWorkspace()) {
			return null;
		}
		return firstPage.getLocationURI();
	}

	private IProject getProject() {
		IScriptProject scriptProject = getScriptProject();
		if (scriptProject != null) {
			return scriptProject.getProject();
		}
		return null;
	}

	public void createProject(IProject project, URI locationURI, IProgressMonitor monitor) throws CoreException {
		PHPProjectUtils.createProjectAt(project, locationURI, monitor);
	}

	public IProject getCurrProject() {
		return getProject();
	}

	protected IncludePath[] setProjectBaseIncludepath() {
		return new IncludePath[] { new IncludePath(getProject(), getProject()) };
	}

	protected BuildpathDetector createBuildpathDetector(IProgressMonitor monitor, IDLTKLanguageToolkit toolkit)
			throws CoreException {
		BuildpathDetector detector = new PHPBuildpathDetector(getProject(), toolkit);
		detector.detectBuildpath(new SubProgressMonitor(monitor, 20));
		return detector;
	}

	protected IPreferenceStore getPreferenceStore() {
		return PHPUiPlugin.getDefault().getPreferenceStore();
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
		try {
			monitor.beginTask(NewWizardMessages.ScriptProjectWizardSecondPage_operation_create, 3);
			if (getProject() == null || !getProject().exists()) {
				updateProject(new SubProgressMonitor(monitor, 1));
			}

			// flushing includepath changes in wizard page
			IWizardPage currentPage = getContainer().getCurrentPage();
			if (!(currentPage instanceof PHPProjectWizardFirstPage)) {
				getBuildPathsBlock().configureScriptProject(monitor);
			}

		} finally {
			monitor.done();
			if (fIsAutobuild != null) {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue());
				fIsAutobuild = null;
			}
		}
	}

	protected void setPhpLangOptions() {

		boolean useASPTags = firstPage.versionGroup.fConfigurationBlock.getUseAspTagsValue();
		PHPVersion phpVersion = firstPage.versionGroup.fConfigurationBlock.getPHPVersionValue();
		ProjectOptions.setSupportingAspTags(useASPTags, getProject());
		ProjectOptions.setPhpVersion(phpVersion, getProject());

	}

	@Override
	public void update(Observable observable, Object object) {
		
		if (observable instanceof BasicSettingsGroup) {
			autoloadGroup.setNamespace(WordUtils.capitalize(firstPage.settingsGroup.getVendor()));
			return;
		}
		
		updateNamespace(autoloadGroup.getNamespace());
	}
	
	protected void updateNamespace(String namespace) {

		Namespace ns = new Namespace();
		ns.setNamespace(namespace);
		ns.add("src");
		
		firstPage.composerPackage.getAutoload().clearPsr0();
		firstPage.composerPackage.getAutoload().getPsr0().add(ns);
	}
}
