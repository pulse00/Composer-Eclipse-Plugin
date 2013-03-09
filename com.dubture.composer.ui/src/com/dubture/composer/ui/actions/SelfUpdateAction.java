package com.dubture.composer.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;

import com.dubture.composer.core.execution.ComposerExecution;
import com.dubture.composer.core.execution.ComposerJsonNotFoundException;
import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPluginImages;

public class SelfUpdateAction extends ComposerAction {

	public SelfUpdateAction(IProject project, IWorkbenchPartSite site) {
		super(project, site, "com.dubture.composer.ui.command.selfupdate");
	}
	
	@Override
	public void run() {
		ComposerExecution exec = new ComposerExecution(project);
		try {
			exec.selfUpdate();
		} catch (PharNotFoundException e) {
			shallInstallComposerPhar(exec);
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ComposerUIPluginImages.SELFUPDATE;
	}
	
}
