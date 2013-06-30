package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.InstallJob;

public class InstallAction extends ComposerAction {

	public InstallAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.install");
	}
	
	@Override
	public void run() {
		ensureSaved();
		
		InstallJob job = new InstallJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.INSTALL;
	}
	
}
