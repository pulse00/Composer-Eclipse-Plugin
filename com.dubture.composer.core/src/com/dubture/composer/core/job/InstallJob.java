package com.dubture.composer.core.job;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dubture.composer.core.log.Logger;

public class InstallJob extends ComposerJob
{
	
    public InstallJob(String composerPhar)
    {
        super("Installing composer dependencies...");
        this.composerPhar = composerPhar;
    }
    
    public InstallJob(IProject project) {
    	super(project, "Installing composer dependencies...");
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
        	int work = project == null ? 2 : 3;
            monitor.beginTask("Running composer.phar install", work);
            monitor.worked(1);
            execute("install", monitor);
            monitor.worked(2);
            
            // refresh project
            if (project != null) {
            	project.refreshLocal(IProject.DEPTH_INFINITE, null);
            	monitor.worked(3);
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
