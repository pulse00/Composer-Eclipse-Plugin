package com.dubture.composer.ui.wizard.projec.template;

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
}
