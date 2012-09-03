package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class RequireHandler extends AbstractHandler implements IHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        WizardDialog dialog = new WizardDialog(shell, new RequireWizard());

        if (dialog.open() == Window.OK) {
            System.out.println("Ok pressed");
        } else {
            System.out.println("Cancel pressed");
        }
        
        return null;
    }
}
