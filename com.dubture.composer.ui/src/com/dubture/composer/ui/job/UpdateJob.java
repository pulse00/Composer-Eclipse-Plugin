package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptLauncher;

public class UpdateJob extends ComposerJob {

	private String[] packages = null;
	
	public UpdateJob(IProject project) {
		super(project, "Updating composer dependencies...");
	}
	
	public void setPackages(String[] packages) {
		this.packages = packages;
	}
	
	public String[] getPackages() {
		return packages;
	}

	protected void launch(ScriptLauncher launcher) throws ExecuteException,
			IOException, InterruptedException {
		
		if (packages == null) {
			launcher.launch("update");
		} else {
			launcher.launch("update", packages);
		}
	}
}
