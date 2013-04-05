package com.dubture.composer.ui.job.runner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.dialogs.MissingExecutableDialog;

public class MissingExecutableRunner implements Runnable {
	@Override
	public void run() {
		try {
			Status status = new Status(IStatus.WARNING, ComposerUIPlugin.PLUGIN_ID, "No PHP executable configured.");
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MissingExecutableDialog dialog = new MissingExecutableDialog(shell, status);
			dialog.open();
		} catch (Exception e2) {
			Logger.logException(e2);
		}
	}
}