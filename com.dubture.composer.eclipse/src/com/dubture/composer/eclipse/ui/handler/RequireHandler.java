package com.dubture.composer.eclipse.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.eclipse.ui.PharNotFoundException;
import com.dubture.composer.eclipse.ui.wizard.require.RequireWizard;

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
        
        new WizardDialog(shell, new RequireWizard(composer)).open();
        return null;
    }
}
