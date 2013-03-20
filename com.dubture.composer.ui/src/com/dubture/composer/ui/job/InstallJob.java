package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;

import com.dubture.composer.core.launch.ComposerLauncher;

public class InstallJob extends ComposerJob {

	public InstallJob(IProject project) {
		super(project, "Installing composer dependencies...");
	}
	
	protected void launch(ComposerLauncher launcher) throws ExecuteException, IOException, InterruptedException {
		launcher.launch("install");
	}
}
