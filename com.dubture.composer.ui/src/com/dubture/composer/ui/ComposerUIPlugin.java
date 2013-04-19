package com.dubture.composer.ui;

import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.pdtextensions.core.ui.preferences.PHPExecutableChangeListener;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class ComposerUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.dubture.composer.ui"; //$NON-NLS-1$

	// The shared instance
	private static ComposerUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ComposerUIPlugin() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("deprecation")
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		try {
			PHPDebugPlugin
					.getDefault()
					.getPluginPreferences()
					.addPropertyChangeListener(new PHPExecutableChangeListener(ComposerPlugin.ID, Keys.PHP_EXECUTABLE));
		} catch (Exception e) {
			Logger.logException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ComposerUIPlugin getDefault() {
		return plugin;
	}

}
