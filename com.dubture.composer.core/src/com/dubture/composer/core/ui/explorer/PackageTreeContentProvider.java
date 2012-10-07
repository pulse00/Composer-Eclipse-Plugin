package com.dubture.composer.core.ui.explorer;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ExternalProjectFragment;
import org.eclipse.dltk.internal.ui.navigator.ScriptExplorerContentProvider;
import org.eclipse.php.internal.ui.explorer.IPHPTreeContentProvider;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.swt.graphics.Image;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.PackagePath;

@SuppressWarnings("restriction")
public class PackageTreeContentProvider extends ScriptExplorerContentProvider implements IPHPTreeContentProvider
{
    public PackageTreeContentProvider()
    {
        super(true);
    }

    @Override
    public void handleProjectChildren(ArrayList<Object> children, IScriptProject project)
    {
        try {
            if (!project.getProject().hasNature(ComposerNature.NATURE_ID)) {
                return;
            }
            ComposerBuildpathContainer container = new ComposerBuildpathContainer(project);
            children.add(container);
            
        } catch (CoreException e) {
            Logger.logException(e);
        }
    }

    @Override   
    public boolean canHandle(Object parentElement)
    {
        if (parentElement instanceof PackagePath) {
            return true;
        }
        
        return parentElement instanceof ComposerBuildpathContainer;
    }

    @Override
    public Object[] handleChildren(Object parentElement)
    {
        if (parentElement instanceof PackagePath) {
            
            PackagePath pPath = (PackagePath) parentElement;
            IScriptProject scriptProject = pPath.getProject();
            IBuildpathEntry entry = pPath.getEntry();
            
            try {
                
                IProjectFragment[] allProjectFragments;
                allProjectFragments = scriptProject.getAllProjectFragments();
                for (IProjectFragment fragment : allProjectFragments) {
                    if (fragment instanceof ExternalProjectFragment) {
                        ExternalProjectFragment external = (ExternalProjectFragment) fragment;
                        if (external.getBuildpathEntry().equals(entry)) {
                            return getChildren(external);
                        }
                    }
                }
            } catch (ModelException e) {
                Logger.logException(e);
                return NO_CHILDREN;
            }
        } else if (parentElement instanceof ComposerBuildpathContainer) {
            ComposerBuildpathContainer container = (ComposerBuildpathContainer) parentElement;
            return container.getChildren();
        }
            
        return NO_CHILDREN;
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof PackagePath) {
            PackagePath path = (PackagePath) element;
            return path.getPackageName();
        }
        
        return null;
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof PackagePath) {
            return PHPPluginImages
                    .get(PHPPluginImages.IMG_OBJS_LIBRARY);
        }
        
        return null;
    }
}
