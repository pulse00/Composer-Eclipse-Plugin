package com.dubture.composer.ui.wizard.project;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.php.internal.core.language.LanguageModelInitializer;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.wizards.PHPProjectWizardFirstPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.pdtextensions.core.ui.PEXUIPlugin;

import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;
import com.dubture.getcomposer.core.objects.Namespace;

@SuppressWarnings("restriction")
public class ComposerProjectWizardSecondPage extends AbstractWizardSecondPage {

	protected static final String FILENAME_BUILDPATH = ".buildpath"; //$NON-NLS-1$
	protected URI currentProjectLocation; // null if location is platform location
	protected AutoloadGroup autoloadGroup;
	private AutoloadValidator validator;
	

	public ComposerProjectWizardSecondPage(AbstractWizardFirstPage mainPage) {
		super(mainPage, "Dependencies");
		setPageComplete(false);
		setTitle("Autoloading settings");
		setDescription("Setup autoloading for your project.");
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
		((ComposerProjectWizardFirstPage)firstPage).settingsGroup.addObserver(this);		

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

	@Override
	public void update(Observable observable, Object object) {
		
		if (observable instanceof BasicSettingsGroup) {
			ComposerProjectWizardFirstPage fPage = (ComposerProjectWizardFirstPage) firstPage;
			autoloadGroup.setNamespace(WordUtils.capitalize(fPage.settingsGroup.getVendor()));
			return;
		}
		
		updateNamespace(autoloadGroup.getNamespace());
	}
	
	protected void updateNamespace(String namespace) {

		Namespace ns = new Namespace();
		ns.setNamespace(namespace);
		ns.add(ComposerPluginConstants.DEFAULT_SRC_FOLDER);
		
		firstPage.getPackage().getAutoload().getPsr0().clear();
		firstPage.getPackage().getAutoload().getPsr0().add(ns);
	}


	@Override
	protected String getPageTitle() {
		return "Autoloading settings";
	}
	
	@Override
	protected String getPageDescription() {
		return "Setup autoloading for your project.";
	}
}
