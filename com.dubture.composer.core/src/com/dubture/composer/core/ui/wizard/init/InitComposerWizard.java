package com.dubture.composer.core.ui.wizard.init;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.getcomposer.core.PHPPackage;

import com.dubture.composer.core.job.DownloadJob;
import com.dubture.composer.core.job.InitializeJob;
import com.dubture.composer.core.log.Logger;

public class InitComposerWizard extends Wizard implements INewWizard
{
    private InitComposerPage page;
    private final IProject project;

    public InitComposerWizard(IProject project)
    {
        this.project = project;
    }

    @Override
    public void addPages()
    {
        page = new InitComposerPage();
        addPage(page);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        
    }

    @Override
    public boolean performFinish()
    {
        IResource phar = project.findMember("composer.phar");
        
        if (page == null || !(page.getPhpPackage() instanceof PHPPackage)) {
            return false;
        }
        
        if (phar == null) {
            
            DownloadJob downloadJob = new DownloadJob(project, "Downloading composer.phar");
            downloadJob.addJobChangeListener(new JobChangeAdapter() {
               
                @Override
                public void done(IJobChangeEvent event)
                {
                    IStatus result = event.getResult();
                    
                    if (result != null && result.isOK()) {
                        initJson();
                    }
                }
            });
            
            downloadJob.schedule();
        } else {
            initJson();
        }
        
        return true;
    }
    
    protected void initJson() {

        try {
            new InitializeJob(project, page.getPhpPackage()).schedule();
        } catch (FileNotFoundException e) {
            Logger.logException(e);
        }
    }
}
