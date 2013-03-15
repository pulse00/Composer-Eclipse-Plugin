package com.dubture.composer.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

public class ComposerUIPluginImages {
	public static final IPath ICONS_PATH = new Path("/icons/full");

	private static final String T_OBJ16 = "obj16";	
	private static final String T_WIZBAN = "wizban";

	public static final ImageDescriptor ADD_DEPENDENCY = create(T_WIZBAN, "packagist.png");
	
	public static final ImageDescriptor AUTHOR = create(T_OBJ16, "author.png");
	public static final ImageDescriptor PAGE = create(T_OBJ16, "page.gif");
	public static final ImageDescriptor PACKAGE = create(T_OBJ16, "package.gif");
	public static final ImageDescriptor PHP = create(T_OBJ16, "php.png");
	public static final ImageDescriptor BROWSER = create(T_OBJ16, "browser.gif");
	public static final ImageDescriptor INSTALL = create(T_OBJ16, "install.gif");
	public static final ImageDescriptor INSTALL_DEV = create(T_OBJ16, "install_dev.gif");
	public static final ImageDescriptor UPDATE = create(T_OBJ16, "update.gif");
	public static final ImageDescriptor UPDATE_NODEV = create(T_OBJ16, "update_nodev.gif");
	public static final ImageDescriptor SELFUPDATE = create(T_OBJ16, "selfupdate.gif");
	public static final ImageDescriptor EVENT = create(T_OBJ16, "event3.png");
	public static final ImageDescriptor SCRIPT = create(T_OBJ16, "script2.png");

	private static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, true);
	}

	private static ImageDescriptor create(String prefix, String name,
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);

		return createImageDescriptor(ComposerUIPlugin.getDefault().getBundle(),
				path, useMissingImageDescriptor);
	}

	public static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

}
