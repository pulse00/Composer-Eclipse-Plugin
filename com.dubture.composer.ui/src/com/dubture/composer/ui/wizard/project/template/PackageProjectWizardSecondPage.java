package com.dubture.composer.ui.wizard.project.template;

import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.job.CreateProjectJob;
import com.dubture.composer.ui.job.CreateProjectJob.JobListener;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class PackageProjectWizardSecondPage extends AbstractWizardSecondPage implements IShellProvider, PackageFilterChangedListener {

	private PackageFilterViewer filter;

	public PackageProjectWizardSecondPage(AbstractWizardFirstPage mainPage, String title) {
		super(mainPage, title);
		setPageComplete(false);
	}
	
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (filter.getSelectedPackage() == null || filter.getSelectedPackage().getSelectedVersion() == null) {
			setPageComplete(false);
		}
	}
	
	@Override
	public void createControl(Composite parent) {
		filter = new PackageFilterViewer();
		filter.createControl(parent);
		filter.addChangeListener(this);
		filter.setMinimumHeight(480);
		setControl(filter.getControl());
		setPageComplete(false);
	}

	@Override
	public void update(Observable o, Object arg) {

	}

	@Override
	protected String getPageTitle() {
		return "Select package";
	}

	@Override
	protected String getPageDescription() {
		return "Search for a package to be used as the startingpoint for your new Composer project.";
	}
	
	@Override
	protected void beforeFinish(IProgressMonitor monitor) throws Exception {

		PackageFilterItem filterItem = filter.getSelectedPackage();
		final CountDownLatch latch = new CountDownLatch(1);
		CreateProjectJob projectJob = new CreateProjectJob(firstPage.nameGroup.getName(), filterItem.getPackage().getName(), filterItem.getSelectedVersion());
		projectJob.setJobListener(new JobListener() {
			@Override
			public void jobStarted() {
				latch.countDown();
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

	@Override
	protected void finishPage(IProgressMonitor monitor) throws Exception {

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


	@Override
	public void filterChanged(PackageFilterItem item) {
		
		if (item != null && item.getSelectedVersion() != null) {
			setPageComplete(true);
			return;
		}
		
		setPageComplete(false);
	}	
}
