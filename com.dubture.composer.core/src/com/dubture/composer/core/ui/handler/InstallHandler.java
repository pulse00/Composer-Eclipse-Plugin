package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import com.dubture.composer.core.job.DownloadJob;
import com.dubture.composer.core.job.InstallJob;
import com.dubture.composer.core.ui.PharNotFoundException;

public class InstallHandler extends ComposerHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            init(event);
        } catch (PharNotFoundException e) {
            installPharDialog(event);
            return null;
        }
        
        if (json == null && ask(event, "No composer.json found", "Would you like to create one?") == SWT.OK) {
            //TODO: create dialog and initialize composer.json
        } else {
            Job job = new InstallJob(composer.getLocation().toOSString());
            job.setUser(true);
            job.schedule();
            return null;
        }
            
        if (ask(event, "No composer.phar found", "Do you want to install composer into this project?") == SWT.OK) {
            Job job = new DownloadJob(project, "Downloading composer.phar...");
            job.setUser(true);
            job.schedule();
        }
        
        return null;
    }
}
