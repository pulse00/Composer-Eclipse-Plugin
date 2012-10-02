package com.dubture.composer.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.UserLibraryBuildpathContainerInitializer;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.ui.statushandlers.StatusManager;
import org.getcomposer.core.Autoload;
import org.getcomposer.core.PackageInterface;

import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class EclipsePHPPackage implements
        NamespaceResolverInterface, InstallableItem
{
    private final PackageInterface phpPackage;

    private IPath path;
    
    public EclipsePHPPackage(PackageInterface phpPackage) {
        
        Assert.isNotNull(phpPackage);
        this.phpPackage = phpPackage;
    }

    @Override
    public IPath resolve(IResource resource)
    {
        Autoload autoload = phpPackage.getAutoload();
        
        if (autoload == null || autoload.getPSR0Path() == null) {
            Logger.debug("Unable to resolve namespace without autoload information " + phpPackage.getName());
            return null;
        }
         
        String targetDir = phpPackage.getTargetDir();
        IPath ns = null;
        IPath path = resource.getFullPath();
        IPath composerPath = getPath();
        
        IPath psr0Path = composerPath.append(autoload.getPSR0Path());
        int segments = psr0Path.segmentCount();
         
        if (path.matchingFirstSegments(psr0Path) == segments) {
             
            if (targetDir != null && targetDir.length() > 0) {
                Path target = new Path(targetDir);
                ns = target.append(path.removeFirstSegments(psr0Path.segmentCount()));    
            } else {
                ns = path.removeFirstSegments(psr0Path.segmentCount());
            }
             
        }
         
        return ns;        
    }

    @Override
    public String getName()
    {
        return phpPackage.getName();
    }

    @Override
    public String getDescription()
    {
        return phpPackage.getDescription();
    }

    @Override
    public String getUrl()
    {
        return phpPackage.getUrl();
    }

    public void setFullPath(String fullPath)
    {
        path = new Path(fullPath);
    }

    public IPath getPath()
    {
        return path;
    }

    public PackageInterface getPhpPackage()
    {
        return phpPackage;
    }
    
    public void createUserLibraryFromPackage(IResource composer, IProgressMonitor monitor) {
        
        IDLTKLanguageToolkit toolkit = PHPLanguageToolkit.getDefault();
        
        String pathString = Path.fromOSString(composer.getRawLocation().toOSString()).removeLastSegments(1).append("vendor").append(getName()).toOSString();
        IPath libPath =  Path.fromOSString(pathString).makeAbsolute();
        
        IPath fullPath = EnvironmentPathUtils.getFullPath(
                EnvironmentManager.getLocalEnvironment(), libPath);
        
        BuildpathContainerInitializer initializer = DLTKCore
                .getBuildpathContainerInitializer(DLTKCore.USER_LIBRARY_CONTAINER_ID);
        if (initializer instanceof UserLibraryBuildpathContainerInitializer) {
            ((UserLibraryBuildpathContainerInitializer) initializer)
                    .setToolkit(toolkit);
        }
        
        IBuildpathContainer suggestedContainer = new ComposerBuildpathContainer(fullPath);
        
        try {
            initializer.requestBuildpathContainerUpdate(suggestedContainer.getPath(), createPlaceholderProject(), suggestedContainer);
        } catch (CoreException e) {
            StatusManager.getManager().handle(e.getStatus());
        }
    }
    
    private class ComposerBuildpathContainer implements IBuildpathContainer {

        private IPath fullPath;

        public ComposerBuildpathContainer(IPath fullPath)
        {
            this.fullPath = fullPath;
        }

        @Override
        public IPath getPath()
        {
            return new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID).append(EclipsePHPPackage.this.getName().replace("/", "_"));
        }
        
        @Override
        public int getKind()
        {
            return IBuildpathContainer.K_SYSTEM;
        }
        
        @Override
        public String getDescription()
        {
            return EclipsePHPPackage.this.getDescription();
        }
        
        @Override
        public IBuildpathEntry[] getBuildpathEntries()
        {
            return new IBuildpathEntry[]{DLTKCore.newLibraryEntry(fullPath, false)};
        }
    }
    
    private static IScriptProject createPlaceholderProject() {
        String name = "####internal"; //$NON-NLS-1$
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        while (true) {
            IProject project = root.getProject(name);
            if (!project.exists()) {
                return DLTKCore.create(project);
            }
            name += '1';
        }
    }
}
