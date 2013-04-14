package com.dubture.composer.ui.wizard.project.template;

import com.dubture.composer.ui.wizard.AbstractComposerWizard;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class PackageProjectCreationWizard extends AbstractComposerWizard {

	@Override
	protected AbstractWizardFirstPage getFirstPage() {
		return new PackageProjectWizardFirstPage();
	}

	@Override
	protected AbstractWizardSecondPage getSecondPage() {
		return new PackageProjectWizardSecondPage(firstPage, "douh");
	}
}
