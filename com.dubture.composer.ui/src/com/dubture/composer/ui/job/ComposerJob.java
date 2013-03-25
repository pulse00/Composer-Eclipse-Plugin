package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.ComposerLauncher;
import com.dubture.composer.core.launch.ComposerPharNotFoundException;
import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;

abstract public class ComposerJob extends Job {
	
	protected IProject project;
	private IProgressMonitor monitor;
	private boolean cancelling = false;
	private ComposerLauncher launcher;

	protected static final IStatus ERROR_STATUS = new Status(Status.ERROR,
			ComposerPlugin.ID,
			"Error running composer, see log for details");

	public ComposerJob(String name) {
		super(name);
	}

	public ComposerJob(IProject project, String name) {
		this(name);
		this.project = project;
	}

	@Override
	protected void canceling() {
		
		if (cancelling || launcher == null || !monitor.isCanceled()) {
			return;
		}
		
		launcher.abort();
		monitor.done();
		cancelling = true;
	}
	
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			
			this.monitor = monitor;

			try {
				launcher = ComposerLauncher.getLauncher(project);
			} catch (ComposerPharNotFoundException e) {
				// run the downloader
				Display.getDefault().asyncExec(new DownloadRunner());
				return Status.OK_STATUS;
			}
			
			launcher.addResponseListener(new ConsoleResponseHandler());
			launcher.addResponseListener(new ExecutionResponseAdapter() {
				public void executionFailed(final String response,
						Exception exception) {
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							
							monitor.done();
							
							String message = "Launching composer failed. See the .metadata/.log file in your workspace for details.";
							if (response != null && response.length() > 0) {
								message = response.trim();
							}
							
							MessageDialog diag = new MessageDialog(
									Display.getDefault().getActiveShell(), 
									getName() + " FAILED!", 
									null, 
									message, 
									MessageDialog.ERROR,
									new String[] {"Ok"},
									0);
							
							
							diag.open();
						};
					});
				}
			});

			monitor.beginTask(getName(), 3);
			monitor.worked(1);
			launch(launcher);
			monitor.worked(2);

			// refresh project
			if (project != null) {
				project.refreshLocal(IProject.DEPTH_INFINITE, null);
				monitor.worked(3);
			}
		} catch (Exception e) {
			Logger.logException(e);
			return ERROR_STATUS;
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}
	
	abstract protected void launch(ComposerLauncher launcher) throws ExecuteException, IOException, InterruptedException;
	
	private class DownloadRunner implements Runnable {

		@Override
		public void run() {
			
			Shell shell = Display.getCurrent().getActiveShell();
			
			if (shell == null) {
				Logger.debug("Unable to get shell for message dialog.");
				return;
			}
			
			if (MessageDialog.openConfirm(shell, "composer.phar not found", "composer.phar can not be found. Download it now?")) {
				DownloadJob job = new DownloadJob(project);
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						// re-schedule the original job
						ComposerJob.this.schedule();
					}
				});
				job.setUser(true);
				job.schedule();
			}
		}
	}
}
