package com.dubture.composer.ui.wizard.projec.template;

import java.util.concurrent.CountDownLatch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.job.CreateProjectJob;
import com.dubture.composer.ui.job.CreateProjectJob.JobListener;
import com.dubture.composer.ui.wizard.project.ComposerProjectWizardFirstPage;
import com.dubture.composer.ui.wizard.project.VersionGroup;
import com.dubture.getcomposer.core.ComposerPackage;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class PackageProjectWizardFirstPage extends ComposerProjectWizardFirstPage {

	private Validator projectTemplateValidator;
	
	public PackageProjectWizardFirstPage() {
		super();
		setPageComplete(false);
		setTitle("Basic Composer Configuration");
		setDescription("Create a new project from existing package");
	}
	
	@Override
	public void createControl(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		initialName = "";
		// create UI elements
		nameGroup = new NameGroup(composite, initialName, getShell());
		settingsGroup = new ProjectTemplateGroup(composite, getShell());
		
		nameGroup.addObserver(this);
		settingsGroup.addObserver(this);
		
		PHPLocationGroup = new LocationGroup(composite, nameGroup, getShell());

		CompositeData data = new CompositeData();
		data.setParetnt(composite);
		data.setSettings(getDialogSettings());
		data.setObserver(PHPLocationGroup);

		versionGroup = new VersionGroup(this, composite);
		//detectGroup = new DetectGroup(composite, PHPLocationGroup, nameGroup);

		nameGroup.addObserver(PHPLocationGroup);

		//PHPLocationGroup.addObserver(detectGroup);
		// initialize all elements
		nameGroup.notifyObservers();
		// create and connect validator
		projectTemplateValidator = new Validator(this);

		nameGroup.addObserver(projectTemplateValidator);
		settingsGroup.addObserver(projectTemplateValidator);
		PHPLocationGroup.addObserver(projectTemplateValidator);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_basic");
		
		setControl(composite);
		composerPackage = new ComposerPackage();
		keywordConverter = new String2KeywordsConverter(composerPackage);
		
	}
	
	@Override
	public void performFinish(final IProgressMonitor monitor) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		CreateProjectJob projectJob = new CreateProjectJob(nameGroup.getName(), ((ProjectTemplateGroup)settingsGroup).projectName.getText(), ((ProjectTemplateGroup)settingsGroup).getVersion());
		projectJob.setJobListener(new JobListener() {
			@Override
			public void jobStarted() {
				synchronized (monitor) {
					latch.countDown();
				}
			}

			@Override
			public void jobFinished(String projectName) {
				latch.countDown();
				refreshProject(projectName);
			}

			@Override
			public void jobFailed() {
				latch.countDown();
			}
		});
		
		projectJob.schedule();
		
		// we need to wait until the first page has started the 
		// create-project composer command and the command actually
		// wrote something to disk, otherwise the command will fail
		//
		// Note: The composer guys do not accept pull requests
		// to allow the create-project command be run on target paths
		// with files in it, so we have to use this workaround.
		try {
			latch.await();
		} catch (InterruptedException e) {
			
		}
	}
	
	protected void refreshProject(String projectName) {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null) {
			Logger.log(ERROR, "Error finishing create-project installation. Could not obtain project from workspace: " + projectName);
			return;
		}
		
		new WorkspaceJob("Refreshing " + projectName) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				return Status.OK_STATUS;
			}
		}.schedule();
	}
}
