package com.dubture.composer.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.wizard.require.RequireWizard;

public class RequireHandler extends ComposerHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        
        try {
            init(event);
        } catch (PharNotFoundException e) {
            installPharDialog(event);
            return null;
        }
        
        if (composer == null) {
            Logger.log(Logger.ERROR, "Error finding composer.json");
            return null;
        }
        
        new WizardDialog(shell, new RequireWizard(composer, scriptProject)).open();
        return null;
    }
}
