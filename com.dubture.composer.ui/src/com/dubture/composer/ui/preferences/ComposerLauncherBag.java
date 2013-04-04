package com.dubture.composer.ui.preferences;

import org.eclipse.php.internal.ui.preferences.util.Key;
import org.pdtextensions.core.ui.preferences.launcher.LauncherKeyBag;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

@SuppressWarnings("restriction")
public class ComposerLauncherBag implements LauncherKeyBag {

	private final Key exeKey = new Key(ComposerPlugin.ID, Keys.PHP_EXECUTABLE); 
	private final Key pharKey = new Key(ComposerPlugin.ID, Keys.COMPOSER_PHAR); 
	private final Key useKey = new Key(ComposerPlugin.ID, Keys.USE_PROJECT_PHAR); 
	
	@Override
	public Key[] getAllKeys() {
		return new Key[]{exeKey, pharKey, useKey};
	}

	@Override
	public Key getPHPExecutableKey() {
		return exeKey;
	}

	@Override
	public Key getScriptKey() {
		return pharKey;
	}

	@Override
	public Key getUseProjectKey() {
		return useKey;
	}
}
