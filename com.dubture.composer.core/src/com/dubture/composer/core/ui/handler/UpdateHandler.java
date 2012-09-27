package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;

import com.dubture.composer.core.job.UpdateJob;
import com.dubture.composer.core.ui.PharNotFoundException;

public class UpdateHandler extends ComposerHandler
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
            UpdateJob job = new UpdateJob(composer.getLocation().toOSString());
            job.setUser(true);
            job.schedule();
        }
            
        
        return null;
    }
}
