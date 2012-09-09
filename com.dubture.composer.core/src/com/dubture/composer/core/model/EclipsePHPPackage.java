package com.dubture.composer.core.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.getcomposer.core.PackageInterface;

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
        // TODO Auto-generated method stub
        return null;
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

}
