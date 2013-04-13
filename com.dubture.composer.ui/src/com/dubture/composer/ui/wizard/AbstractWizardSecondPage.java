package com.dubture.composer.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.dltk.internal.ui.wizards.BuildpathDetector;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.dltk.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.eclipse.php.internal.ui.wizards.IPHPProjectCreateWizardPage;
import org.eclipse.php.internal.ui.wizards.PHPBuildpathDetector;
import org.eclipse.php.ui.util.PHPProjectUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.pdtextensions.core.exception.ExecutableNotFoundException;
import org.pdtextensions.core.launch.ScriptLauncher;
import org.pdtextensions.core.launch.ScriptLauncherManager;
import org.pdtextensions.core.ui.PEXUIPlugin;

import com.dubture.composer.core.launch.environment.ComposerEnvironmentFactory;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;
import com.dubture.composer.ui.job.runner.MissingExecutableRunner;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.VersionedPackage;
import com.dubture.getcomposer.core.objects.Namespace;
import com.dubture.getcomposer.packages.PharDownloader;

@SuppressWarnings("restriction")
public abstract class AbstractWizardSecondPage extends CapabilityConfigurationPage implements IPHPProjectCreateWizardPage, Observer {

	protected final AbstractWizardFirstPage firstPage;
	protected Boolean fIsAutobuild;
	protected ScriptLauncher launcher;
	protected PharDownloader downloader;

	@Inject
	protected ScriptLauncherManager launchManager;

	public AbstractWizardSecondPage(AbstractWizardFirstPage mainPage, String title) {
		super(title);
		setPageComplete(false);
		setTitle(getPageTitle());
		setDescription(getPageDescription());
		firstPage = mainPage;
		ContextInjectionFactory.inject(this, PEXUIPlugin.getDefault().getEclipseContext());
	}

	@Override
	public void initPage() {
		changeToNewProject();
	}

	@Override
	protected String getScriptNature() {
		return PHPNature.ID;
	}

	protected void changeToNewProject() {
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

	protected void dumpAutoload(final IProgressMonitor monitor) throws Exception {

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

	protected void installComposer(IProgressMonitor monitor) throws CoreException {
		downloader = new PharDownloader();
		InputStream resource = downloader.download();
		IFile file = getProject().getFile("composer.phar");
		file.create(resource, true, monitor);
		file.refreshLocal(IResource.DEPTH_ZERO, monitor);
	}

	protected void addComposerJson(IProgressMonitor monitor) throws CoreException {

		IFile file = getProject().getFile(com.dubture.getcomposer.core.ComposerConstants.COMPOSER_JSON);
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
				IPath path = new Path(com.dubture.composer.core.ComposerPluginConstants.DEFAULT_SRC_FOLDER).append(ns
						.getNamespace());
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
		boolean useASPTags = false;
		PHPVersion phpVersion = firstPage.versionGroup.fConfigurationBlock.getPHPVersionValue();
		ProjectOptions.setSupportingAspTags(useASPTags, getProject());
		ProjectOptions.setPhpVersion(phpVersion, getProject());
	}

	protected URI getProjectLocationURI() throws CoreException {
		if (firstPage.isInWorkspace()) {
			return null;
		}
		return firstPage.getLocationURI();
	}

	protected IProject getProject() {
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
	
	public void cancel() {
		if (downloader != null) {
			downloader.abort();
		}
	}
	
	abstract protected void updateProject(IProgressMonitor monitor) throws CoreException, InterruptedException;
	abstract public void update(Observable o, Object arg);
	abstract protected String getPageTitle();
	abstract protected String getPageDescription();
	public abstract void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException;
	
}
