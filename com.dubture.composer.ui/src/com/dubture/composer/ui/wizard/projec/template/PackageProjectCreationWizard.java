package com.dubture.composer.ui.wizard.projec.template;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.ui.wizard.project.ComposerProjectCreationWizard;
import com.dubture.composer.ui.wizard.project.ComposerProjectWizardSecondPage;

public class PackageProjectCreationWizard extends ComposerProjectCreationWizard {

	@Override
	public void addPages() {

		firstPage = new PackageProjectWizardFirstPage();
		addPage(firstPage);

		secondPage = new ComposerProjectWizardSecondPage(firstPage);
		addPage(secondPage);

		lastPage = secondPage;

	}
	
	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		
		if (firstPage != null) {
			firstPage.performFinish(monitor);
			
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (secondPage != null) {
			secondPage.performFinish(monitor);
		}
	}

}
