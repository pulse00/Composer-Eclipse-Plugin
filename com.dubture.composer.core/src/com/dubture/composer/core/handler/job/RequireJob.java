package com.dubture.composer.core.handler.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pex.core.log.Logger;

public class RequireJob extends ComposerJob
{
    private String dependency;

    public RequireJob(String composer, String dependency)
    {
        super("Adding dependency...");
        this.dependency = dependency;
        this.composer = composer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            monitor.subTask("Installing dependency: " + dependency);
            execute("require " + dependency);
            monitor.worked(1);
        } catch (Exception e) {
            Logger.logException(e);
            return ERROR_STATUS;
        }
        
        return Status.OK_STATUS;
    }
}
