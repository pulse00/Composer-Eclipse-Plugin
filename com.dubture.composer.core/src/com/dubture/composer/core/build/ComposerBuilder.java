package com.dubture.composer.core.build;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.core.ComposerPlugin;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ComposerBuilder extends IncrementalProjectBuilder
{
    public final static String BUILDER_ID = ComposerPlugin.ID + ".composerBuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args,
            IProgressMonitor monitor) throws CoreException
    {
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        return null;
    }
    
    protected void fullBuild(final IProgressMonitor monitor)
            throws CoreException {
        try {
            getProject().accept(new ComposerVisitor());
        } catch (CoreException e) {
        }
    }
    
    protected void incrementalBuild(IResourceDelta delta,
            IProgressMonitor monitor) throws CoreException {
        delta.accept(new ComposerDeltaVisitor());
    }    
}
