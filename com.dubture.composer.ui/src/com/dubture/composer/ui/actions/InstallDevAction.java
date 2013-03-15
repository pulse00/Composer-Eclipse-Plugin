package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;
import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.InstallDevJob;

public class InstallDevAction extends ComposerAction {

	public InstallDevAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.installDev");
	}
	
	@Override
	public void run() {
		InstallDevJob job = new InstallDevJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.INSTALL_DEV;
	}
	
}
