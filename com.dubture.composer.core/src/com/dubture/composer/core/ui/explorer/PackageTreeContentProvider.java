package com.dubture.composer.core.ui.explorer;

import java.util.ArrayList;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.ui.explorer.IPHPTreeContentProvider;

@SuppressWarnings("restriction")
public class PackageTreeContentProvider implements IPHPTreeContentProvider
{

    @Override
    public void handleProjectChildren(ArrayList<Object> children, IScriptProject project)
    {
        ComposerBuildpathContainer container = new ComposerBuildpathContainer(project);
        children.add(container);
    }

    @Override   
    public boolean canHandle(Object parentElement)
    {
        return parentElement instanceof ComposerBuildpathContainer;
    }

    @Override
    public Object[] handleChildren(Object parentElement)
    {
        if (!(parentElement instanceof ComposerBuildpathContainer)) {
            return new Object[0];
        }
        
        ComposerBuildpathContainer container = (ComposerBuildpathContainer) parentElement;
        return container.getChildren();
    }
}
