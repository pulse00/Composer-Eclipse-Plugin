package com.dubture.composer.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import com.dubture.composer.core.ComposerBuildpathContainerInitializer;

public class ComposerBuildpathContainer implements IBuildpathContainer {

    private IScriptProject project;

    public ComposerBuildpathContainer(IScriptProject project)
    {
        this.project = project;
    }

    @Override
    public IPath getPath()
    {
        return new Path(ComposerBuildpathContainerInitializer.PACKAGE_PATH);
    }
    
    @Override
    public int getKind()
    {
        return IBuildpathContainer.K_APPLICATION;
    }
    
    @Override
    public String getDescription()
    {
        return "Composer";
    }
    
    @Override
    public IBuildpathEntry[] getBuildpathEntries()
    {
        // this needs to be create from the indexed vendor/composer/installed.json
        String pathString = "/Users/sobert/Desktop/php-ffmpeg";
        
        IPath libPath =  Path.fromOSString(pathString).makeAbsolute();
        
        IPath fullPath = EnvironmentPathUtils.getFullPath(
                EnvironmentManager.getLocalEnvironment(), libPath);
        
        return new IBuildpathEntry[]{DLTKCore.newLibraryEntry(fullPath, new IAccessRule[0], new IBuildpathAttribute[0], false, true)};
    }
}