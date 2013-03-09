package com.dubture.composer.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;

import com.dubture.composer.core.job.DownloadJob;
import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.core.log.Logger;

abstract public class ComposerHandler extends AbstractHandler
{
    protected IResource composer;
    protected IResource json;
    protected IScriptProject scriptProject;
    protected IProject project;
    
    public ComposerHandler(IProject project) {
    	this.project = project;
    }
    
    public ComposerHandler() {
    	this(null);
    }
    
    protected int ask(ExecutionEvent event, String message, String question)
    {
        MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event),
                SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        dialog.setText(message);
        dialog.setMessage(question);
        return dialog.open();
    }
   
    @SuppressWarnings("unchecked")
	protected void init(ExecutionEvent event) throws PharNotFoundException {

//    	System.out.println("ComposerHandler.init");
    	
    	
    	
		// try find project from active selection if project not present yet
		if (project == null) {
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			
			if (selection != null && selection instanceof IStructuredSelection) {
			      
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
			      
				for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
			          
					Object element = iterator.next();
			          
					if (element instanceof IModelElement) {
						IModelElement model = (IModelElement) element;
						IScriptProject scriptProject = model.getScriptProject();
						project = scriptProject.getProject();
						break;
					}
				}
			}
		}
    	

		composer = project.findMember("composer.phar");
		
		if (composer == null) {
		    Logger.log(Logger.WARNING_DEBUG, "Phar not found in project " + project.getName());
		    throw new PharNotFoundException();
		}
		
		json = project.findMember("composer.json");
    }
    
    protected void installPharDialog(ExecutionEvent event) {
        
        if (ask(event, "No composer.phar found", "Do you want to install composer into this project?") == SWT.OK) {
            new DownloadJob(project, "Downloading composer.phar...").schedule();
        }
    }
}
