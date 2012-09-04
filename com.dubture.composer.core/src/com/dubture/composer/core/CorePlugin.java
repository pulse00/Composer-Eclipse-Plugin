/*******************************************************************************
 * This file is part of the PHPPackage eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.composer.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.dubture.composer.core.packagist.SearchResultDownloader;

public class CorePlugin extends Plugin {

    private static CorePlugin plugin;
    
    public static final String ID = "com.dubture.composer.core";

    private static final String DEBUG = "com.dubture.composer.core/debug";
    
    private SearchResultDownloader packageDownloader = null;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
	    super.start(bundleContext);
	    
	    plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
	    
	    super.stop(bundleContext);
	    plugin = null;
	}
	
	public static CorePlugin getDefault()
	{
	    return plugin;
	}
	
    public static void debug(String message)
    {
        if (plugin == null) {
            // tests
            System.err.println(message);
            return;
        }
        
        String debugOption = Platform.getDebugOption(DEBUG);
        
        if (plugin.isDebugging() && "true".equalsIgnoreCase(debugOption)) {
            plugin.getLog().log(new Status(Status.INFO, ID, message));
        }
    }
    
    public static void logException(Exception e) 
    {
        IStatus status = new Status(Status.ERROR, CorePlugin.ID, e.getMessage(), e); 
        plugin.getLog().log(status);
    }
    
    public SearchResultDownloader getPackageDownloader() {
        
        if (packageDownloader != null) {
            return packageDownloader;
        }
        
        return packageDownloader = new SearchResultDownloader();
    }
}
