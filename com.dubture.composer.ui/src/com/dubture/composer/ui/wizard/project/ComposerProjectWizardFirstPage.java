package com.dubture.composer.ui.wizard.project;

import java.util.Observable;

import org.eclipse.ui.PlatformUI;

import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.wizard.AbstractValidator;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;

@SuppressWarnings("restriction")
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
			if (settingsGroup.getVendor() != null && nameGroup.getName() != null) {
				composerPackage.setName(String.format("%s/%s", settingsGroup.getVendor(), nameGroup.getName()));
			}

			if (settingsGroup.getDescription().length() > 0) {
				composerPackage.setDescription(settingsGroup.getDescription());
			}

			if (settingsGroup.getLicense().length() > 0) {
				composerPackage.getLicense().clear();
				composerPackage.getLicense().add(settingsGroup.getLicense());
			}

			if (settingsGroup.getType().length() > 0) {
				composerPackage.setType(settingsGroup.getType());
			}

			if (settingsGroup.getKeywords().length() > 0) {
				keywordConverter.convert(settingsGroup.getKeywords());
			}
		}
	}

	protected AbstractValidator getValidator() {
		return new Validator(this);
	}

	@Override
	protected void afterLocationGroup() {

	}

	@Override
	protected void installHelp() {
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_basic");
	}
}
