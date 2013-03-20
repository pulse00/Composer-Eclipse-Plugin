package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.SelfUpdateJob;

public class SelfUpdateAction extends ComposerAction {

	public SelfUpdateAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.selfupdate");
	}
	
	@Override
	public void run() {
		SelfUpdateJob job = new SelfUpdateJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.SELFUPDATE;
	}
	
}
