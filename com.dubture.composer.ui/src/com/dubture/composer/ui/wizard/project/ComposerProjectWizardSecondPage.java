package com.dubture.composer.ui.wizard.project;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.VersionedPackage;
import org.getcomposer.core.objects.Namespace;
import org.getcomposer.packages.PharDownloader;
import org.pdtextensions.core.exception.ExecutableNotFoundException;
import org.pdtextensions.core.launch.ScriptLauncher;
import org.pdtextensions.core.launch.ScriptLauncherManager;
import org.pdtextensions.core.ui.PEXUIPlugin;

import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.core.launch.environment.ComposerEnvironmentFactory;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;
import com.dubture.composer.ui.job.runner.MissingExecutableRunner;

@SuppressWarnings("restriction")
public class ComposerProjectWizardSecondPage extends CapabilityConfigurationPage implements IPHPProjectCreateWizardPage, Observer {

	protected static final String FILENAME_BUILDPATH = ".buildpath"; //$NON-NLS-1$
	protected final ComposerProjectWizardFirstPage firstPage;
	protected URI currentProjectLocation; // null if location is platform location
	protected AutoloadGroup autoloadGroup;
	private Boolean fIsAutobuild;
	private AutoloadValidator validator;
	private PharDownloader downloader;
	private ScriptLauncher launcher;
	
	@Inject
	private ScriptLauncherManager launchManager;

	public ComposerProjectWizardSecondPage(ComposerProjectWizardFirstPage mainPage) {
		super("Dependencies");
		setPageComplete(false);
		setTitle("Autoloading settings");
		setDescription("Setup autoloading for your project.");
		firstPage = mainPage;
		currentProjectLocation = null;
		fIsAutobuild = null;
		ContextInjectionFactory.inject(this, PEXUIPlugin.getDefault().getEclipseContext());
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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_autoload");
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
			monitor.beginTask("Initializing project", 70);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			createProject(getProject(), currentProjectLocation, new SubProgressMonitor(monitor, 20));

			IBuildpathEntry[] buildpathEntries = null;
			
			//TODO: see https://github.com/pulse00/Composer-Eclipse-Plugin/issues/37
			IPath srcPath = new Path(ComposerPluginConstants.DEFAULT_SRC_FOLDER);

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
			cpEntries.add(DLTKCore.newSourceEntry(projectPath.append("vendor").append("composer")));

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
			
			monitor.beginTask("Initializing buildpaths", 10);
			if (getProject() == null || !getProject().exists()) {
				updateProject(new SubProgressMonitor(monitor, 3));
			}

			// flushing includepath changes in wizard page
			IWizardPage currentPage = getContainer().getCurrentPage();
			if (!(currentPage instanceof PHPProjectWizardFirstPage)) {
				getBuildPathsBlock().configureScriptProject(monitor);
			}
			
			monitor.setTaskName("Creating project structure");
			addComposerJson(monitor);
			monitor.worked(4);
			
			monitor.setTaskName("Installing composer.phar");
			installComposer(monitor);
			monitor.worked(4);
			
			monitor.setTaskName("Dumping autoloader");
			dumpAutoload(monitor);
			monitor.worked(2);
		
		} catch(Exception e) { 
			Logger.logException(e);
		} finally {
			monitor.done();
			if (fIsAutobuild != null) {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue());
				fIsAutobuild = null;
			}
		}
	}

	private void dumpAutoload(final IProgressMonitor monitor) throws Exception {

		try {
			launcher = launchManager.getLauncher(ComposerEnvironmentFactory.FACTORY_ID, getProject());
		} catch (ExecutableNotFoundException e) {
			Display.getDefault().asyncExec(new MissingExecutableRunner());
			return;
		}
		launcher.addResponseListener(new ConsoleResponseHandler());		
		try {
			launcher.launch("dumpautoload");
			getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (Exception e) {
			Logger.logException(e);
		}
	}

	private void installComposer(IProgressMonitor monitor) throws CoreException {
		downloader = new PharDownloader();
		InputStream resource = downloader.download();
		IFile file = getProject().getFile("composer.phar");
		file.create(resource, true, monitor);
		file.refreshLocal(IResource.DEPTH_ZERO, monitor);
	}
	
	private void addComposerJson(IProgressMonitor monitor) throws CoreException {
		
		IFile file = getProject().getFile(org.getcomposer.core.ComposerConstants.COMPOSER_JSON);
		Namespace ns = firstPage.getPackage().getAutoload().getPsr0().getFirst();
		
		if (ns != null) {
			if (ns.getNamespace().contains("\\")) {
				String[] split = ns.getNamespace().split("\\\\");
				IPath path = new Path(com.dubture.composer.core.ComposerPluginConstants.DEFAULT_SRC_FOLDER);
				for (String segment : split) {
					path = path.append(segment);
					IFolder folder = getProject().getFolder(path);
					if (!folder.exists()) {
						folder.create(false, true, monitor);					
					}
				}
			} else {
				IPath path = new Path(com.dubture.composer.core.ComposerPluginConstants.DEFAULT_SRC_FOLDER).append(ns.getNamespace());
				IFolder folder = getProject().getFolder(path);
				if (!folder.exists()) {
					folder.create(false, true, monitor);
				}
			}
		}
		
		if (file.exists()) {
			Logger.debug("composer.json already exists in the location");
			return;
		}
		
		ComposerPackage composerPackage = firstPage.getPackage();
		VersionedPackage phpVersion = new VersionedPackage();
		phpVersion.setName("php");
		phpVersion.setVersion(">=" + firstPage.getPHPVersionValue().getAlias().replace("php", ""));
		composerPackage.getRequire().add(phpVersion);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(composerPackage.toJson().getBytes());
		file.create(bis, true, monitor);
		getProject().refreshLocal(0, monitor);
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
		ns.add(ComposerPluginConstants.DEFAULT_SRC_FOLDER);
		
		firstPage.composerPackage.getAutoload().getPsr0().clear();
		firstPage.composerPackage.getAutoload().getPsr0().add(ns);
	}

	public void cancel() {
		if (downloader != null) {
			downloader.abort();
		}
	}
}
