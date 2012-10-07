package com.dubture.composer.core.build;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.core.ComposerPlugin;

public class ComposerBuilder extends IncrementalProjectBuilder
{
    public final static String BUILDER_ID = ComposerPlugin.ID + ".composerBuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args,
            IProgressMonitor monitor) throws CoreException
    {
        getProject().accept(new ComposerVisitor());
        return null;
    }
}
