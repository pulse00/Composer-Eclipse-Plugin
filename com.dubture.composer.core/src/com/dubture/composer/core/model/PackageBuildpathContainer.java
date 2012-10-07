package com.dubture.composer.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import com.dubture.composer.core.ComposerBuildpathContainerInitializer;

public class PackageBuildpathContainer implements IBuildpathContainer
{
    private IPath sourcePath;

    public PackageBuildpathContainer(IPath sourcePath) {
        this.sourcePath = sourcePath;
        
    }
    
    @Override
    public IBuildpathEntry[] getBuildpathEntries()
    {
        IPath libPath =  sourcePath.makeAbsolute();
        IPath fullPath = EnvironmentPathUtils.getFullPath(
                EnvironmentManager.getLocalEnvironment(), libPath);
        
        return new IBuildpathEntry[]{
                DLTKCore.newLibraryEntry(sourcePath, new IAccessRule[0], new IBuildpathAttribute[0], false, true)
        };        
    }

    @Override
    public String getDescription()
    {
        return sourcePath.lastSegment();
    }

    @Override
    public int getKind()
    {
        return IBuildpathContainer.K_APPLICATION;
    }

    @Override
    public IPath getPath()
    {
        return new Path(ComposerBuildpathContainerInitializer.CONTAINER);
    }
}
