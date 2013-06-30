package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.job.UpdateJob;

public class UpdateAction extends ComposerAction {
	
	public UpdateAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.update");
	}
	
	
	@Override
	public void run() {
		ensureSaved();
		
		UpdateJob job = new UpdateJob(project);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.UPDATE;
	}
}
