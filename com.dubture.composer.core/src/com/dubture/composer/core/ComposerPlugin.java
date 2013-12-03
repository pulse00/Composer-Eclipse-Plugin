/*******************************************************************************
 * This file is part of the PHPPackage eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.composer.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.dubture.composer.core.internal.resources.ComposerProject;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.core.resources.IComposerProject;

public class ComposerPlugin extends AbstractUIPlugin {

	private static ComposerPlugin plugin;

	public static final String ID = "com.dubture.composer.core";

	private static final String DEBUG = "com.dubture.composer.core/debug";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);

		plugin = this;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				if (event.getType() == IResourceChangeEvent.PRE_DELETE
						&& event.getResource() instanceof IProject) {
					ModelAccess.getInstance().getPackageManager()
							.removeProject((IProject) event.getResource());
				}
			}
		};
		workspace.addResourceChangeListener(listener);
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {

		super.stop(bundleContext);
		plugin = null;
	}

	public static ComposerPlugin getDefault() {
		return plugin;
	}

	public static void debug(String message) {
		if (plugin == null) {
			// tests
			return;
		}

		String debugOption = Platform.getDebugOption(DEBUG);

		if (plugin.isDebugging() && "true".equalsIgnoreCase(debugOption)) {
			plugin.getLog().log(new Status(Status.INFO, ID, message));
		}
	}

	public static void logException(Exception e) {
		IStatus status = new Status(Status.ERROR, ComposerPlugin.ID,
				e.getMessage(), e);
		plugin.getLog().log(status);
	}

	public IEclipsePreferences getProjectPreferences(IProject project) {
		ProjectScope ps = new ProjectScope(project);
		return ps.getNode(ID);
	}

	public boolean isBuildpathContainerEnabled() {
		return getPreferenceStore().getBoolean(
				ComposerPluginConstants.PREF_BUILDPATH_ENABLE);
	}
	
	public IComposerProject getComposerProject(IProject project) {
		if (project == null) {
			return null;
		}
		return new ComposerProject(project);
	}
}
