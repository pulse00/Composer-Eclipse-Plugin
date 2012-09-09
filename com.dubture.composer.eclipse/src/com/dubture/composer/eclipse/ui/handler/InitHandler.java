package com.dubture.composer.eclipse.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.dubture.composer.eclipse.ui.PharNotFoundException;
import com.dubture.composer.eclipse.ui.wizard.init.InitComposerWizard;

public class InitHandler extends ComposerHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell();

        try {

            init(event);

            if (json instanceof IResource) {
                MessageBox dialog = new MessageBox(
                        HandlerUtil.getActiveShell(event), SWT.ICON_QUESTION
                                | SWT.OK);
                dialog.setText("Composer already installed");
                dialog.setMessage("It seems composer is already installed in this project (" + composer.getFullPath().toOSString() + ")");
                return null;
            }

        } catch (PharNotFoundException e) {
            
        }
        
        if (project == null) {
            MessageBox dialog = new MessageBox(
                    HandlerUtil.getActiveShell(event), SWT.ICON_QUESTION
                            | SWT.OK);
            dialog.setText("Composer error");
            dialog.setMessage("Error initializing composer for this project");
            return null;
        }

        new WizardDialog(shell, new InitComposerWizard(project)).open();
        return null;
    }

}
