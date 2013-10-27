package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.UpdateDevJob;

public class UpdateDevAction extends ComposerAction {
	
	public UpdateDevAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.updateDev");
	}
	
	
	@Override
	public void run() {
		ensureSaved();
		
		UpdateDevJob job = new UpdateDevJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.UPDATE_DEV;
	}
}
