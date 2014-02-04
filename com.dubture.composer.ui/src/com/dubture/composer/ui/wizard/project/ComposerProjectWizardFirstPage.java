package com.dubture.composer.ui.wizard.project;

import java.util.Observable;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.wizard.AbstractValidator;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;

public class ComposerProjectWizardFirstPage extends AbstractWizardFirstPage {

	public BasicSettingsGroup settingsGroup;

	public ComposerProjectWizardFirstPage() {
		super("Basic Composer Configuration");
		setPageComplete(false);
		setTitle("Basic Composer Configuration");
		setDescription("Setup your new composer project");
	}

	@Override
	protected void beforeLocationGroup() {
		settingsGroup = new BasicSettingsGroup(composite, getShell());
		settingsGroup.addObserver(this);
		settingsGroup.addObserver(validator);
	}

	@Override
	protected void finishControlSetup() {
	}

	@Override
	public void initPage() {

	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof BasicSettingsGroup) {
			updatePackageFromSettingsGroup(settingsGroup);
		}
	}

	protected AbstractValidator getValidator() {
		return new Validator(this);
	}

	@Override
	protected void afterLocationGroup() {

	}

	@Override
	protected void setHelpContext(Control container) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_basic");
	}
}
