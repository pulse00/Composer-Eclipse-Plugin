package com.dubture.composer.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = ComposerPlugin.getDefault().getPreferenceStore();
		prefs.setValue(Keys.USE_PROJECT_PHAR, true);
	}
}
