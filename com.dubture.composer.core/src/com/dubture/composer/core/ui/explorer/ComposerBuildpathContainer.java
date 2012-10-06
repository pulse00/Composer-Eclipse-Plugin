package com.dubture.composer.core.ui.explorer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;

import com.dubture.composer.core.model.ModelAccess;

@SuppressWarnings("restriction")
public class ComposerBuildpathContainer extends BuildPathContainer
{
    private IScriptProject iScriptProject;

    public ComposerBuildpathContainer(IScriptProject parent)
    {
        super(parent, DLTKCore.newContainerEntry(parent.getPath()));
        this.iScriptProject = parent;
    }
    
    public String getLabel() {
        return "Composer Packages";
    }
    
    @Override
    public IAdaptable[] getChildren()
    {
        return ModelAccess.getInstance().getPackageManager().getPackagePaths(iScriptProject);
    }
}
