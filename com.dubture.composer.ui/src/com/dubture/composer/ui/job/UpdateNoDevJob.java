package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptLauncher;

public class UpdateNoDevJob extends ComposerJob {

	public UpdateNoDevJob(IProject project) {
		super(project, "Updating composer dependencies (no-dev)...");
	}

	protected void launch(ScriptLauncher launcher) throws ExecuteException,
			IOException, InterruptedException {
		launcher.launch("update", "--no-dev");
	}
}
