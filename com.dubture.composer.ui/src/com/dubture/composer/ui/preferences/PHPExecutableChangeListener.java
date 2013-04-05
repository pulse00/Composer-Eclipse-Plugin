package com.dubture.composer.ui.preferences;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.ui.preferences.util.PreferencesSupport;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

@SuppressWarnings({ "deprecation", "restriction" })
public class PHPExecutableChangeListener implements IPropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if (!"org.eclipse.php.debug.coreinstalledPHPDefaults".equals(event.getProperty())) {
			return;
		}
		
		IPreferenceStore store = ComposerPlugin.getDefault().getPreferenceStore();
		PreferencesSupport prefSupport = new PreferencesSupport(ComposerPlugin.ID, store);
		String executable = prefSupport.getPreferencesValue(Keys.PHP_EXECUTABLE, null, null);
		
		if (executable != null && executable.length() > 0) {
			return;
		}

 		try {
 			PHPexeItem[] exes = PHPexes.getInstance().getAllItems();
 			
 			if (exes.length == 1) {
 				store.setValue(Keys.PHP_EXECUTABLE, exes[0].getExecutable().toString());
 			}	

		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}
