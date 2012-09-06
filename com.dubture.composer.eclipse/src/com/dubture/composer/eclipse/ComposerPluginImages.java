package com.dubture.composer.eclipse;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

public class ComposerPluginImages
{
    public static final IPath ICONS_PATH = new Path("/ico/full"); //$NON-NLS-1$

    private static final String T_WIZBAN = "wizban"; //$NON-NLS-1$

    public static final ImageDescriptor DESC_WIZBAN_ADD_DEPENDENCY = create(
            T_WIZBAN, "packagist.png");//$NON-NLS-1$

    private static ImageDescriptor create(String prefix, String name)
    {
        return create(prefix, name, true);
    }

    private static ImageDescriptor create(String prefix, String name,
            boolean useMissingImageDescriptor)
    {
        IPath path = ICONS_PATH.append(prefix).append(name);

        return createImageDescriptor(ComposerPlugin.getDefault().getBundle(), path,
                useMissingImageDescriptor);
    }

    public static ImageDescriptor createImageDescriptor(Bundle bundle,
            IPath path, boolean useMissingImageDescriptor)
    {
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
