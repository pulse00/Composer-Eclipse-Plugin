package com.dubture.composer.ui.wizard.projec.template;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IFileHandle;

import com.dubture.composer.ui.wizard.AbstractValidator;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.ValidationException;

@SuppressWarnings("restriction")
public final class Validator extends AbstractValidator {

	public Validator(AbstractWizardFirstPage composerProjectWizardFirstPage) {
		super(composerProjectWizardFirstPage);
	}

	@Override
	protected void beginValidation() throws ValidationException {
		
		IPath fileLocation = firstPage.PHPLocationGroup.getLocation();
		if (fileLocation.toPortableString().length() > 0) {
			final IFileHandle directory = environment.getFile(fileLocation);
			IPath futurepath = directory.getPath().append(firstPage.nameGroup.getName());
			File futureFile = futurepath.toFile();
			if ((futureFile.exists() && futureFile.isFile()) || (futureFile.exists() && futureFile.isDirectory() && futureFile.list().length > 0)) {
				this.firstPage.setErrorMessage("The target directory is not empty. Unable to run \"create-project\" with a target directory containing files.");
				this.firstPage.setMessage(null);
				this.firstPage.setPageComplete(false);
				throw new ValidationException();
			}
		}
	}

	@Override
	protected void finishValidation() throws ValidationException {

	}
}
