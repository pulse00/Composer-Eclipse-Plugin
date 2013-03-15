package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.UpdateNoDevJob;

public class UpdateNoDevAction extends ComposerAction {
	
	public UpdateNoDevAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.updateNoDev");
	}
	
	
	@Override
	public void run() {
		UpdateNoDevJob job = new UpdateNoDevJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.UPDATE_NODEV;
	}
}
