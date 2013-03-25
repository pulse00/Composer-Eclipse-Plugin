package com.dubture.composer.ui.wizard.project;

import java.util.Observable;
import java.util.Observer;

import com.dubture.composer.core.validation.ValidationUtils;

public final class AutoloadValidator implements Observer {

	private ComposerProjectWizardSecondPage secondPage;
	
	AutoloadValidator(ComposerProjectWizardSecondPage secondPage) {
		this.secondPage = secondPage;
	}

	@Override
	public void update(Observable observable, Object object) {
		
		String namespace = secondPage.autoloadGroup.getNamespace();
		
		if (!ValidationUtils.validateNamespace(namespace)) {
			secondPage.setErrorMessage("You must use a valid PHP namespace for psr-0 autoloading.");
			secondPage.setMessage("Enter a PHP namespace name.");
			secondPage.setPageComplete(false);
			return;
		}
		
		secondPage.setPageComplete(true);
		secondPage.setErrorMessage(null);
		secondPage.setMessage(null);
		
	}
}
