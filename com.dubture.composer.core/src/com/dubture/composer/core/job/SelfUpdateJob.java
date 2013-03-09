package com.dubture.composer.core.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dubture.composer.core.log.Logger;

public class SelfUpdateJob extends ComposerJob
{

    public SelfUpdateJob(String composer)
    {
        super("Self-Updating composer...");
        this.composerPhar = composer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            monitor.beginTask("Running composer.phar self-update", 2);
            monitor.worked(1);
            execute("selfupdate", monitor);
            monitor.worked(1);
        } catch (Exception e) {
            Logger.logException(e);
            return ERROR_STATUS;
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
