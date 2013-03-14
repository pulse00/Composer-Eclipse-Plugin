package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;

import com.dubture.composer.core.launch.ComposerLauncher;

public class UpdateJob extends ComposerJob {

	public UpdateJob(IProject project) {
		super(project, "Update composer dependencies...");
	}

	protected void launch(ComposerLauncher launcher) throws ExecuteException,
			IOException, InterruptedException {
		launcher.launch("update");
	}
}
