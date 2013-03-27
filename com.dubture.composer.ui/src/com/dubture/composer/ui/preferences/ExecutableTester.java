package com.dubture.composer.ui.preferences;

import java.io.File;
import java.net.URL;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.osgi.framework.Bundle;

import com.dubture.composer.core.launch.execution.ComposerExecutor;
import com.dubture.composer.core.launch.execution.ExecutionResponseListener;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPlugin;

@SuppressWarnings("restriction")
public class ExecutableTester implements Runnable {

	private PHPexeItem phPexeItem;
	private ExecutionResponseListener listener;

	public ExecutableTester(PHPexeItem phPexeItem, ExecutionResponseListener listener) {
		this.phPexeItem = phPexeItem;
		this.listener = listener;
	}

	@Override
	public void run() {
		
		try {
			ComposerExecutor executor = new ComposerExecutor();
			CommandLine cmd = new CommandLine(phPexeItem.getExecutable());
			cmd.addArgument("testexecutable");
			
			Bundle bundle = Platform.getBundle(ComposerUIPlugin.PLUGIN_ID);
			URL entry = bundle.getEntry("Resources/launcher");
			
			File file = new File(FileLocator.resolve(entry).toURI());
			
			if (file != null) {
				executor.setWorkingDirectory(file);
			}

			executor.addResponseListener(listener);
			executor.execute(cmd);
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}
