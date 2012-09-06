package com.dubture.composer.eclipse.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;

abstract public class ComposerHandler extends AbstractHandler
{
    protected IResource composer;
    protected IResource json;
    protected IProject project;
    
    protected int ask(ExecutionEvent event, String message, String question)
    {
        MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event),
                SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        dialog.setText(message);
        dialog.setMessage(question);
        return dialog.open();
        
    }
    
    @SuppressWarnings("unchecked")
    protected void init(ExecutionEvent event) {
        
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        
        if (selection != null && selection instanceof IStructuredSelection) {
            
            IStructuredSelection strucSelection = (IStructuredSelection) selection;
            
            for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
                
                Object element = iterator.next();
                
                if (element instanceof IModelElement) {
                    IModelElement model = (IModelElement) element;
                    project = model.getScriptProject().getProject();
                    composer = project.findMember("composer.phar");
                    json = project.findMember("composer.json");
                    return;
                }
            }
        }
    }
}
