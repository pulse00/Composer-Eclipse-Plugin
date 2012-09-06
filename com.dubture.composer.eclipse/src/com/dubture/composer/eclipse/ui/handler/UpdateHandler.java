package com.dubture.composer.eclipse.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;

import com.dubture.composer.eclipse.job.DownloadJob;
import com.dubture.composer.eclipse.job.UpdateJob;

public class UpdateHandler extends ComposerHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        init(event);
        
        if (composer != null) {
            if (json == null && ask(event, "No composer.json found", "Would you like to create one?") == SWT.OK) {
                //TODO: create dialog and initialize composer.json
            } else {
                new UpdateJob(composer.getLocation().toOSString()).schedule();
            }
        } else {
            
            if (ask(event, "No composer.phar found", "Do you want to install composer into this project?") == SWT.OK) {
                new DownloadJob(project, "Downloading composer.phar...").schedule();
            }
        }
        
        return null;
    }
}
