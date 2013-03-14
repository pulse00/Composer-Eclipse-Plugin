package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.ComposerLauncher;
import com.dubture.composer.core.launch.ComposerPharNotFoundException;
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
		MessageBox dialog = new MessageBox(Display.getDefault()
				.getActiveShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
		dialog.setText("composer.phar not found");
		dialog.setMessage("composer.phar can not be found. Download it now?");
		
		if (dialog.open() == SWT.OK) {
			DownloadJob job = new DownloadJob(project);
			job.setUser(true);
			job.schedule();
			schedule();
			return Status.OK_STATUS;
		} else {
			return ERROR_STATUS;
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ComposerLauncher launcher = null;

			try {
				launcher = ComposerLauncher.getLauncher(project);
			} catch (ComposerPharNotFoundException e) {
				return shallInstallComposerPhar();
			}
			
			launcher.addResponseHandler(new ConsoleResponseHandler(monitor));

			monitor.beginTask("Running composer.phar install", 3);
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

//	private void doExecute(String[] arguments, IProgressMonitor monitor)
//			throws IOException, InterruptedException, CoreException {
//		new DefaultExecutableLauncher().launch(composerPhar, arguments,
//				new ConsoleResponseHandler(monitor));
//	}
}
