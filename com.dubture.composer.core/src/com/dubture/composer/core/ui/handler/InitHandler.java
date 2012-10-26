package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.ui.PharNotFoundException;
import com.dubture.composer.core.ui.wizard.init.InitComposerWizard;

public class InitHandler extends ComposerHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell();

        try {
            init(event);
        } catch (PharNotFoundException e) {
        }

        if (json instanceof IResource) {
            showWarning("Composer already installed",
                    "It seems composer is already installed in this project");
            return null;
        }

        if (scriptProject == null) {
            showWarning("Composer error",
                    "Error initializing composer for this project");
            return null;
        }

        new WizardDialog(shell, new InitComposerWizard(scriptProject)).open();
        return null;
    }

    protected void showWarning(final String title, final String content)
    {
        Display.getDefault().asyncExec(new Runnable()
        {

            @Override
            public void run()
            {
                MessageDialog.openWarning(
                        Display.getDefault().getActiveShell(), title, content);
            }
        });
    }
}
