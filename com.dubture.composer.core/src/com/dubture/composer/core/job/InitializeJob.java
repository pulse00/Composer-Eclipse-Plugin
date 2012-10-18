package com.dubture.composer.core.job;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.getcomposer.core.Author;
import org.getcomposer.core.PHPPackage;

import com.dubture.composer.core.log.Logger;

public class InitializeJob extends ComposerJob
{
    private final PHPPackage phpPackage;
    private IProject project;
    private IResource json = null;
    
    public InitializeJob(IProject project, PHPPackage phpPackage) throws FileNotFoundException
    {
        super("Installing composer dependencies...");
        
        Assert.isNotNull(phpPackage);
        Assert.isNotNull(project);
        
        this.phpPackage = phpPackage;
        this.project = project;
        
        IResource phar = project.findMember("composer.phar");
        
        if (phar == null) {
            throw new FileNotFoundException("Cannot initialize a project without composer.phar");
        }
        
        composer = phar.getLocation().toOSString();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        
        
        try {
            monitor.beginTask("Running composer.phar init", 3);
            monitor.worked(1);
            
            List<String> args = new ArrayList<String>();
            
            args.add("--name=" + phpPackage.name);
            
            if (phpPackage.description != null && phpPackage.description.length() > 0) {
                args.add(String.format("--description=%s", phpPackage.description));
            }
            
            if (phpPackage.authors != null && phpPackage.authors.length > 0) {
                Author author = phpPackage.authors[0];
                if (author.name != null && author.email != null) {
                    args.add(String.format("--author=%s", author.getInitString()));
                }
            }
            
            args.add("--minimum-stability=" + phpPackage.minimumStability);
            args.add("--no-interaction");
            
            
            execute("init", args.toArray(new String[args.size()]), monitor);
            monitor.worked(2);
            
            project.refreshLocal(IResource.DEPTH_ONE, monitor);
            
            json = project.findMember("composer.json");
            
            
            monitor.worked(3);
            
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logException(e);
            return ERROR_STATUS;
        } finally {
            monitor.done();
        }
        
        // open the file
        if (json instanceof IFile) {
            
            Display.getDefault().asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        
                        IFile file = (IFile) json;
                        IWorkbench workbench = PlatformUI.getWorkbench();
                        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                        IWorkbenchPage page = window.getActivePage();
                        
                        IEditorDescriptor desc = PlatformUI.getWorkbench().
                                getEditorRegistry().getDefaultEditor(file.getName());
                        page.openEditor(new FileEditorInput(file), desc.getId());
                    } catch (Exception e) {
                        Logger.logException(e);
                    }                
                }
            });
            
        }
        
        return Status.OK_STATUS;
    }
}
