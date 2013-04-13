package com.dubture.composer.ui.wizard.project;

import org.eclipse.dltk.ui.DLTKUIPlugin;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.wizard.AbstractComposerWizard;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;

public class ComposerProjectCreationWizard extends AbstractComposerWizard {

	public static final String SELECTED_PROJECT = "SelectedProject";
	
	public ComposerProjectCreationWizard() {
		setDefaultPageImageDescriptor(ComposerUIPluginImages.CREATE_PROJECT);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Composer Project");
	}
	
	@Override
	protected AbstractWizardFirstPage getFirstPage() {
		return new ComposerProjectWizardFirstPage();
	}

	@Override
	protected AbstractWizardSecondPage getSecondPage() {
		return new ComposerProjectWizardSecondPage(firstPage);
	}
}
