package com.dubture.composer.core.ui.view.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import com.dubture.composer.core.build.InstalledPackage;

public class GraphContentProvider extends ArrayContentProvider implements
        IGraphEntityContentProvider
{
    List<InstalledPackage> packages;
    
    public GraphContentProvider(List<InstalledPackage> packages)
    {
        this.packages = packages;
    }

    @Override
    public Object[] getConnectedTo(Object entity)
    {
        List<InstalledPackage> connections = new ArrayList<InstalledPackage>();
        
        if (!(entity instanceof InstalledPackage)) {
            return null;
        }
        
        InstalledPackage currentPackage = (InstalledPackage) entity;
        
        for (InstalledPackage pack : packages) {
            
            if (currentPackage.requires(pack)) {
                connections.add(pack);
            }
        }
        
        return connections.toArray();
    }

    public void setPackages(List<InstalledPackage> packages2)
    {
        this.packages = packages2;
    }
}
