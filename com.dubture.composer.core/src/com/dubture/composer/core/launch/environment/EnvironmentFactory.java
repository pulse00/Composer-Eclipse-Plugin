package com.dubture.composer.core.launch.environment;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;
import com.dubture.composer.core.preferences.PreferencesSupport;

public class EnvironmentFactory {

	public static final int ENV_SYS_COMPOSER = 1;
	public static final int ENV_SYS_PHP_SYS_PHAR = 2;
	public static final int ENV_PDT_PHP_SYS_PHAR = 3;
	public static final int ENV_SYS_PHP_PRJ_PHAR = 4;
	public static final int ENV_PDT_PHP_PRJ_PHAR = 5;
	
	
	public static Environment getEnvironment(IProject project) {
		
		IPreferenceStore prefs = ComposerPlugin.getDefault().getPreferenceStore();
		int env = prefs.getInt(ComposerPluginConstants.PREF_ENVIRONMENT);
		
		Environment e;
		
		PreferencesSupport prefSupport = new PreferencesSupport(ComposerPlugin.ID, prefs);
		String executable = prefSupport.getPreferencesValue(Keys.PHP_EXECUTABLE, null, project);
		
		String useProjectPhar = prefSupport.getPreferencesValue(Keys.USE_PROJECT_PHAR, null, project);
		String systemPhar = prefSupport.getPreferencesValue(Keys.COMPOSER_PHAR, null, project);
		
		if (executable != null && executable.length() > 0) {
			
			System.err.println(useProjectPhar);
			
			if (useProjectPhar != null && "true".equals(useProjectPhar) || (systemPhar == null || systemPhar.length() == 0) ) {
				return new SysPhpPrjPhar(executable);
			}
			
			return new SysPhpSysPhar(executable, systemPhar);
		}
		
		// preference for environment found
		if (env > 0 && env <= 5) {
			e = create(env);
			
			if (e.isAvailable()) {
				return e;
			}
		}
		
		// no preference for environment present
		if (env == 0) {
			String composer = EnvironmentFinder.findComposer();
			
			if (composer != null) {
				prefs.setValue(ComposerPluginConstants.PREF_ENVIRONMENT, ENV_SYS_COMPOSER);
				return create(ENV_SYS_COMPOSER);
			}
			
			String composerPhar = EnvironmentFinder.findComposerPhar();
			String php = null;
			String pdt = null;
			
			if (composerPhar != null) {
				
				php = EnvironmentFinder.findPhp();
				if (php != null && php.length() > 0) {
					prefs.setValue(ComposerPluginConstants.PREF_ENVIRONMENT, ENV_SYS_PHP_SYS_PHAR);
					return create(ENV_SYS_PHP_SYS_PHAR);
				}
				
				pdt = EnvironmentFinder.findPdtPhp();
				if (pdt != null && pdt.length() > 0) {
					prefs.setValue(ComposerPluginConstants.PREF_ENVIRONMENT, ENV_PDT_PHP_SYS_PHAR);
					return create(ENV_PDT_PHP_SYS_PHAR);	
				}
			}
			
			if (php == null || php.length() == 0) {
				php = EnvironmentFinder.findPhp();
			}
			
			if (php != null && php.length() > 0) {
				prefs.setValue(ComposerPluginConstants.PREF_ENVIRONMENT, ENV_SYS_PHP_PRJ_PHAR);
				return create(ENV_SYS_PHP_PRJ_PHAR);
			}
			
			if (pdt == null || php.length() == 0) {
				pdt = EnvironmentFinder.findPdtPhp();
			}
			
			if (pdt != null && pdt.length() > 0) {
				prefs.setValue(ComposerPluginConstants.PREF_ENVIRONMENT, ENV_PDT_PHP_PRJ_PHAR);
				return create(ENV_PDT_PHP_PRJ_PHAR);	
			}
		}
		
		return null;
	}
	
	public static Environment create(int type) {
		switch (type) {
		case ENV_SYS_COMPOSER:
			return new SysComposer();
			
		case ENV_SYS_PHP_SYS_PHAR:
			return new SysPhpSysPhar();
			
		case ENV_PDT_PHP_SYS_PHAR:
			return new PdtPhpSysPhar();
			
		case ENV_SYS_PHP_PRJ_PHAR:
			return new SysPhpPrjPhar();
			
		case ENV_PDT_PHP_PRJ_PHAR:
			return new PdtPhpPrjPhar();
		}
		return null;
	}
}
