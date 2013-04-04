package com.dubture.composer.ui.job;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pdtextensions.core.launch.ScriptLauncher;
import org.pdtextensions.core.launch.ScriptLauncherManager;
import org.pdtextensions.core.launch.ScriptNotFoundException;
import org.pdtextensions.core.launch.execution.ExecutionResponseAdapter;
import org.pdtextensions.core.ui.PEXUIPlugin;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.environment.ComposerEnvironmentFactory;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;
import com.dubture.composer.ui.job.runner.ComposerFailureMessageRunner;

abstract public class ComposerJob extends Job {
	
	private IProject project;
	private IProgressMonitor monitor;
	private boolean cancelling = false;
	private ScriptLauncher launcher;
	
	@Inject
	public ScriptLauncherManager manager;
	
	protected static final IStatus ERROR_STATUS = new Status(Status.ERROR,
			ComposerPlugin.ID,
			"Error running composer, see log for details");

	public ComposerJob(String name) {
		super(name);
		
		ContextInjectionFactory.inject(this, PEXUIPlugin.getDefault().getEclipseContext());		
	}

	public ComposerJob(IProject project, String name) {
		this(name);
		this.setProject(project);
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
				launcher = manager.getLauncher(ComposerEnvironmentFactory.FACTORY_ID, getProject());
				/*
			} catch (ExecutableNotFoundException e) {
				// inform the user of the missing executable
				Display.getDefault().asyncExec(new MissingExecutableRunner());
				return Status.OK_STATUS;
				*/
			} catch (ScriptNotFoundException e) {
				// run the downloader
				Display.getDefault().asyncExec(new DownloadRunner());
				return Status.OK_STATUS;
			}
			
			launcher.addResponseListener(new ConsoleResponseHandler());
			launcher.addResponseListener(new ExecutionResponseAdapter() {
				public void executionFailed(final String response, final Exception exception) {
					Display.getDefault().asyncExec(new ComposerFailureMessageRunner(response, monitor));
				}
				
				@Override
				public void executionMessage(String message) {
					if (monitor != null && message != null) {
						monitor.subTask(message);
						monitor.worked(1);
					}
				}
			});

			monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
			monitor.worked(1);
			launch(launcher);
			monitor.worked(1);

			// refresh project
			if (getProject() != null) {
				getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
				monitor.worked(1);
			}
		} catch (Exception e) {
			Logger.logException(e);
			return ERROR_STATUS;
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}
	
	abstract protected void launch(ScriptLauncher launcher) throws ExecuteException, IOException, InterruptedException;
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	private class DownloadRunner implements Runnable {

		@Override
		public void run() {
			
			Shell shell = Display.getCurrent().getActiveShell();
			
			if (shell == null) {
				Logger.debug("Unable to get shell for message dialog.");
				return;
			}
			
			if (MessageDialog.openConfirm(shell, "composer.phar not found", "composer.phar can not be found. Download it now?")) {
				DownloadJob job = new DownloadJob(getProject());
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
