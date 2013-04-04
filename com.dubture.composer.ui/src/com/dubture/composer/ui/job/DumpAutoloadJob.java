package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptLauncher;

public class DumpAutoloadJob extends ComposerJob {

	public DumpAutoloadJob(IProject project, String name) {
		super(project, name);
	}

	@Override
	protected void launch(ScriptLauncher launcher) throws ExecuteException, IOException, InterruptedException {
		launcher.launch("dumpautoload");
	}
}
