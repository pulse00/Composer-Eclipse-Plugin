package com.dubture.composer.ui.job.runner;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.dubture.composer.core.launch.ComposerLauncher;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.dialogs.ComposerJobFailureDialog;

public class ComposerFailureMessageRunner implements Runnable {

	private final String response;
	private final IProgressMonitor monitor;

	public ComposerFailureMessageRunner(String response, IProgressMonitor monitor) {
		this.response = response;
		this.monitor = monitor;
	}

	public void run() {

		if (monitor != null) {
			monitor.done();
		}

		ComposerLauncher.reserEnvironment();
		String message = "Launching composer failed. See the .metadata/.log file in your workspace for details.";
		if (response != null && response.length() > 0) {
			message = response.trim();
		}

		try {
			new ComposerJobFailureDialog("", new Status(Status.ERROR, ComposerUIPlugin.PLUGIN_ID, message)).open();
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}