package com.dubture.composer.core.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;

public class PackagePath implements IAdaptable
{
    private IBuildpathEntry entry;
    
    private String name;

    private IScriptProject scriptProject;

    public PackagePath(IBuildpathEntry entry, IScriptProject scriptProject)
    {
        this.entry = entry;
        this.scriptProject = scriptProject;
        IPath path = entry.getPath();
        int num = path.segmentCount() - 1;
        StringBuilder builder = new StringBuilder();
        builder.append(path.segment(num-2));
        builder.append("/");
        builder.append(path.segment(num-1));
        builder.append(" (");
        builder.append(path.segment(num));
        builder.append(")");
        name = builder.toString();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter)
    {
        return null;
    }

    public String getPackageName()
    {
        return name;
    }

    public IBuildpathEntry getEntry()
    {
        return entry;
    }
    
    public IScriptProject getProject() {
        return scriptProject;
    }
}
