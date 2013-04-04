package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptLauncher;

public class UpdateJob extends ComposerJob {

	public UpdateJob(IProject project) {
		super(project, "Updating composer dependencies...");
	}

	protected void launch(ScriptLauncher launcher) throws ExecuteException,
			IOException, InterruptedException {
		launcher.launch("update");
	}
}
