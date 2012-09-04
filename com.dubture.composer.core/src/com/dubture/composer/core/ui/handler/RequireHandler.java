package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.ui.wizard.require.RequireWizard;

public class RequireHandler extends ComposerHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        init(event);
        WizardDialog dialog = new WizardDialog(shell, new RequireWizard(composer));
        dialog.open();
        return null;
    }
}
