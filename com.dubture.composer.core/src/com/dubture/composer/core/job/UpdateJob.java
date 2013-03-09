package com.dubture.composer.core.job;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dubture.composer.core.log.Logger;

public class UpdateJob extends ComposerJob
{

    public UpdateJob(String composer)
    {
        super("Updating composer dependencies...");
        this.composerPhar = composer;
    }
    
    public UpdateJob(IProject project) {
    	super(project, "Updating composer dependencies...");
    }


    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
        	int work = project == null ? 2 : 3;
            monitor.beginTask("Running composer.phar update", work);
            monitor.worked(1);
            execute("update", monitor);
            monitor.worked(1);
            
            // refresh project
            if (project != null) {
            	project.refreshLocal(IProject.DEPTH_INFINITE, null);
            	monitor.worked(1);
            }
        } catch (Exception e) {
            Logger.logException(e);
            return ERROR_STATUS;
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
