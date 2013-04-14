package com.dubture.composer.ui.wizard.project.template;

import org.eclipse.dltk.ui.DLTKUIPlugin;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.wizard.AbstractComposerWizard;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class PackageProjectCreationWizard extends AbstractComposerWizard {

	public PackageProjectCreationWizard() {
		
		setDefaultPageImageDescriptor(ComposerUIPluginImages.CREATE_PROJECT_FROM_PACKAGE);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Composer Project from existing package");
		
	}
	@Override
	protected AbstractWizardFirstPage getFirstPage() {
		return new PackageProjectWizardFirstPage();
	}

	@Override
	protected AbstractWizardSecondPage getSecondPage() {
		return new PackageProjectWizardSecondPage(firstPage, "douh");
	}
}
