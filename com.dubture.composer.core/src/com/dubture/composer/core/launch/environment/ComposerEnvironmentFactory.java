package com.dubture.composer.core.launch.environment;

import org.eclipse.jface.preference.IPreferenceStore;
import org.pdtextensions.core.launch.environment.AbstractEnvironmentFactory;
import org.pdtextensions.core.launch.environment.PrjPharEnvironment;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.ComposerPreferenceConstants;

public class ComposerEnvironmentFactory extends AbstractEnvironmentFactory {

	public static final String FACTORY_ID = "com.dubture.composer.core.launcherfactory";
	
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return ComposerPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getPluginId() {
		return ComposerPlugin.ID;
	}

	@Override
	protected PrjPharEnvironment getProjectEnvironment(String executable) {
		return new SysPhpPrjPhar(executable);
	}

	@Override
	protected String getExecutableKey() {
		return ComposerPreferenceConstants.PHP_EXECUTABLE;
	}

	@Override
	protected String getUseProjectKey() {
		return ComposerPreferenceConstants.USE_PROJECT_PHAR;
	}

	@Override
	protected String getScriptKey() {
		return ComposerPreferenceConstants.COMPOSER_PHAR;
	}
}
