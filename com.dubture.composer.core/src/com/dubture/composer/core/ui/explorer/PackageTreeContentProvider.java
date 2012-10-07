package com.dubture.composer.core.ui.explorer;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.ui.explorer.IPHPTreeContentProvider;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.PackagePath;

@SuppressWarnings("restriction")
public class PackageTreeContentProvider implements IPHPTreeContentProvider
{

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
        /*
        
        if (parentElement instanceof PackagePath) {
            PackagePath path = (PackagePath) parentElement;
            
            if(path.getEntry() instanceof IBuildpathEntry) {
                IBuildpathEntry entry = (IBuildpathEntry) path.getEntry();
                
                if (ComposerBuildpathContainerInitializer.CONTAINER.equals(entry.getPath().segment(0))) {

                    String pathString = "/Users/sobert/Desktop/php-ffmpeg";
                    if (!entry.getPath().segment(1).contains("ffmpeg")) {
                        pathString = "/Users/sobert/Documents/workspaces/runtime-PDTExtensions/asdf/vendor/monolog/monolog";
                    }
                    
                    IScriptProject scriptProject = DLTKCore.create(path.getProject());
                    IPath libPath =  Path.fromOSString(pathString).makeAbsolute();
                    
                    IPath fullPath = EnvironmentPathUtils.getFullPath(
                            EnvironmentManager.getLocalEnvironment(), libPath);
                    
                    try {
                        IPath sPath = new Path(ComposerBuildpathContainerInitializer.CONTAINER);
                        IBuildpathContainer container = DLTKCore.getBuildpathContainer(sPath, scriptProject);
                        
                        if (container instanceof com.dubture.composer.core.model.ComposerBuildpathContainer) {
                            
                            com.dubture.composer.core.model.ComposerBuildpathContainer composerContainer = (com.dubture.composer.core.model.ComposerBuildpathContainer) container;
                            
                            IBuildpathEntry[] buildpathEntries = composerContainer.getBuildpathEntries();
                            IBuildpathEntry libraryEntry = DLTKCore.newLibraryEntry(fullPath, new IAccessRule[0], new IBuildpathAttribute[0], false, true);
                            
                            
//                            return new IPath[]{libraryEntry.getSourceAttachmentPath()};
                            
                            return buildpathEntries;
                        }
                        
                        
                    } catch (ModelException e) {
                        e.printStackTrace();
                    }
                    
                    
                    return new IBuildpathEntry[]{
                            DLTKCore.newLibraryEntry(fullPath, new IAccessRule[0], new IBuildpathAttribute[0], false, true)
                    };                    
                    
                }
            }
        }

        */
        if (!(parentElement instanceof ComposerBuildpathContainer)) {
            return new Object[0];
        }
        
        ComposerBuildpathContainer container = (ComposerBuildpathContainer) parentElement;
        return container.getChildren();
    }
}
