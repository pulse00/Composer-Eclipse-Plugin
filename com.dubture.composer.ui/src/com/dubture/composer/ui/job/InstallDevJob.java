package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;

import com.dubture.composer.core.launch.ComposerLauncher;

public class InstallDevJob extends ComposerJob {

	public InstallDevJob(IProject project) {
		super(project, "Installing composer dependencies (dev)...");
	}
	
	protected void launch(ComposerLauncher launcher) throws ExecuteException, IOException, InterruptedException {
		launcher.launch("install", "--dev");
	}
}
