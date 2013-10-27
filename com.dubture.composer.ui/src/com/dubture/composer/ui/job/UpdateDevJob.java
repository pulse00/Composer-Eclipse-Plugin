package com.dubture.composer.ui.job;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptLauncher;

public class UpdateDevJob extends ComposerJob {

	private String[] packages = null;
	
	public UpdateDevJob(IProject project) {
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
			launcher.launch("update", "--dev");
		} else {
			launcher.launch("update", (String[])ArrayUtils.addAll(new String[] {"--dev"}, packages));
		}
	}
}
