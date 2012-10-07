package com.dubture.composer.core.build;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import com.dubture.composer.core.model.ModelAccess;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class ComposerVisitor implements IResourceVisitor
{
    @Override
    public boolean visit(IResource resource) throws CoreException
    {
        if(resource.getName().equals("installed.json")) {
            ModelAccess.getInstance().getPackageManager().updateBuildpath();
            return false;
        }
        return true;
    }
}
