package com.dubture.composer.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.ModelException;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.ModelAccess;

/**
 * Handler to update the composer builder ?!?
 * 
 */
public class UpdateProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

//		if (this.scriptProject != null) {
//			try {
//				ModelAccess.getInstance().getPackageManager()
//						.updateBuildpath(scriptProject.getProject());
//				BuildpathUtil.setupVendorBuildpath(this.scriptProject,
//						new NullProgressMonitor());
//			} catch (ModelException e) {
//				Logger.logException(e);
//			}
//		}

		return null;
	}
}
