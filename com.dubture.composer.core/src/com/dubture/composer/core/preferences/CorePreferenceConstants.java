package com.dubture.composer.core.preferences;

import com.dubture.composer.core.ComposerPlugin;

public class CorePreferenceConstants {
	
	public interface Keys {
		public static final String PHP_EXECUTABLE = ComposerPlugin.ID + "php_executable";
		public static final String COMPOSER_PHAR = ComposerPlugin.ID + "composer_phar";
		public static final String USE_PROJECT_PHAR = ComposerPlugin.ID + "use_project_phar";
	}
	
}
