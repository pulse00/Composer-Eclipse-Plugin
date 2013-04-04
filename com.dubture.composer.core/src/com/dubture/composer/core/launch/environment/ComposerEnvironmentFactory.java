package com.dubture.composer.core.launch.environment;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.pdtextensions.core.launch.environment.Environment;
import org.pdtextensions.core.launch.environment.EnvironmentFactory;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;
import com.dubture.composer.core.preferences.PreferencesSupport;

public class ComposerEnvironmentFactory implements EnvironmentFactory {

	public static final String FACTORY_ID = "com.dubture.composer.core.launcherfactory";
	
	public Environment getEnvironment(IProject project) {
		
		IPreferenceStore prefs = ComposerPlugin.getDefault().getPreferenceStore();
		
		PreferencesSupport prefSupport = new PreferencesSupport(ComposerPlugin.ID, prefs);
		String executable = prefSupport.getPreferencesValue(Keys.PHP_EXECUTABLE, null, project);
		
		String useProjectPhar = prefSupport.getPreferencesValue(Keys.USE_PROJECT_PHAR, null, project);
		String systemPhar = prefSupport.getPreferencesValue(Keys.COMPOSER_PHAR, null, project);
		
		if (executable != null && executable.length() > 0) {
			if (useProjectPhar != null && "true".equals(useProjectPhar) || (systemPhar == null || systemPhar.length() == 0) ) {
				return new SysPhpPrjPhar(executable);
			}
			
			return new SysPhpSysPhar(executable, systemPhar);
		}
		
		return null;
	}
}
