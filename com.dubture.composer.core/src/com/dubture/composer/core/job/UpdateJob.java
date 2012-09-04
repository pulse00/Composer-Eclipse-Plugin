package com.dubture.composer.core.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pex.core.log.Logger;

public class UpdateJob extends ComposerJob
{

    public UpdateJob(String composer)
    {
        super("Updating composer dependencies...");
        this.composer = composer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            execute("update");
        } catch (Exception e) {
            Logger.logException(e);
            return ERROR_STATUS;
        }
        
        return Status.OK_STATUS;
    }
}
