package com.dubture.composer.ui.wizard.project;

import com.dubture.composer.ui.wizard.AbstractValidator;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.ValidationException;

public final class Validator extends AbstractValidator {
	
	public Validator(AbstractWizardFirstPage composerProjectWizardFirstPage) {
		super(composerProjectWizardFirstPage);
	}

	@Override
	protected void finishValidation() throws ValidationException {
		ComposerProjectWizardFirstPage first = (ComposerProjectWizardFirstPage) firstPage;
		final String vendor = first.settingsGroup.getVendor();
		
		if (vendor == null || vendor.length() == 0) {
			firstPage.setErrorMessage(null);
			firstPage.setMessage("Enter a vendor name.");
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}
	}

	@Override
	protected void beginValidation() throws ValidationException {
		
	}
}