package com.dubture.composer.core.model;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.core.includepath.IncludePath;

@SuppressWarnings("restriction")
public class PackagePath extends IncludePath
{
    public PackagePath(Object entry, IScriptProject scriptProject)
    {
        super(entry, scriptProject);
    }
}
