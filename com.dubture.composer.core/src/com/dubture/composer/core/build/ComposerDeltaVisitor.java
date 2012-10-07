package com.dubture.composer.core.build;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.core.model.PackageManager;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ComposerDeltaVisitor implements IResourceDeltaVisitor
{

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException
    {
        IResource resource = delta.getResource();
        PackageManager packageManager = ModelAccess.getInstance().getPackageManager();
        
        switch (delta.getKind()) {
            case IResourceDelta.ADDED :
                if (resource instanceof IFile && "installed.json".equals(resource.getName())) {
                    packageManager.updateBuildpath();
                }
                break;
            case IResourceDelta.REMOVED :
                
                if (resource instanceof IProject) {
                    packageManager.removeProject((IProject)resource);
                }
                break;
            case IResourceDelta.CHANGED :
                if (resource instanceof IFile && "installed.json".equals(resource.getName())) {
                    packageManager.updateBuildpath();
                }
                break;
        }
        // return true to continue visiting children.
        return true;
    }
}
