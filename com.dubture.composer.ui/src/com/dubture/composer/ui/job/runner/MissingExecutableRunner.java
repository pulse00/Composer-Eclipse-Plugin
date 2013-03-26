package com.dubture.composer.ui.job.runner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.dialogs.MissingExecutableDialog;

public class MissingExecutableRunner implements Runnable {
	@Override
	public void run() {
		try {
			String PID = ComposerUIPlugin.PLUGIN_ID;
			MultiStatus info = new MultiStatus(PID, IStatus.WARNING, "Missing php executable", null);
			//TODO: find a way to link the corresponding PHP preference page in the dialog.
			info.add(new Status(IStatus.WARNING, PID, "Executing composer.phar failed due to a missing php executable. \n\nPlease setup a PHP valid executable in the PHP preference page."));
			
			Shell shell = Display.getCurrent().getActiveShell();
			MissingExecutableDialog dialog = new MissingExecutableDialog(shell, "title", "messate", info, IStatus.CANCEL | IStatus.ERROR | IStatus.OK);
			dialog.open();
			
//			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Composer job failed", "Unable to execute composer.phar", info);
		} catch (Exception e2) {
			Logger.logException(e2);
		}
	}
}