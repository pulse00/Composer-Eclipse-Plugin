package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.ComposerLauncher;
import com.dubture.composer.core.launch.ComposerPharNotFoundException;
import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;

abstract public class ComposerJob extends Job {
	
	protected IProject project;

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

	protected IStatus shallInstallComposerPhar() {
		
		InstallRunner runner = new InstallRunner();
		Display.getDefault().syncExec(runner);
		return runner.getStatus();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			ComposerLauncher launcher = null;

			try {
				launcher = ComposerLauncher.getLauncher(project);
			} catch (ComposerPharNotFoundException e) {
				return shallInstallComposerPhar();
			}
			
			launcher.addResponseListener(new ConsoleResponseHandler());
			launcher.addResponseListener(new ExecutionResponseAdapter() {
				public void executionFailed(final String response,
						Exception exception) {
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							
							monitor.done();
							
							MessageDialog diag = new MessageDialog(
									Display.getDefault().getActiveShell(), 
									getName() + " FAILED!", 
									null, 
									response.trim(), 
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
	
	private class InstallRunner implements Runnable {

		private IStatus status = ERROR_STATUS;
		
		@Override
		public void run() {
			
			Shell shell = Display.getCurrent().getActiveShell();
			
			if (shell == null) {
				Logger.debug("Unable to get shell for message dialog.");
				return;
			}
			
			MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			dialog.setText("composer.phar not found");
			dialog.setMessage("composer.phar can not be found. Download it now?");
			
			if (dialog.open() == SWT.OK) {
				DownloadJob job = new DownloadJob(project);
				job.setUser(true);
				job.schedule();
				schedule();
				status = Status.OK_STATUS;
			} else {
				status = ERROR_STATUS;
			}
		}
		
		public IStatus getStatus() {
			return status;
		}
	}
}
