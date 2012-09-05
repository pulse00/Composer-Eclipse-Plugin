package com.dubture.composer.core.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pex.core.log.Logger;

public class InstallJob extends ComposerJob
{
    public InstallJob(String composer)
    {
        super("Installing composer dependencies...");
        this.composer = composer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            monitor.beginTask("Running composer.phar install", 2);
            monitor.worked(1);
            execute("install");
            monitor.worked(2);
        } catch (Exception e) {
            Logger.logException(e);
            return ERROR_STATUS;
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
