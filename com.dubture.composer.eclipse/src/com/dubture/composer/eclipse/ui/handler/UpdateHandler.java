package com.dubture.composer.eclipse.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;

import com.dubture.composer.eclipse.job.UpdateJob;
import com.dubture.composer.eclipse.ui.PharNotFoundException;

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
            new UpdateJob(composer.getLocation().toOSString()).schedule();
        }
            
        
        return null;
    }
}
