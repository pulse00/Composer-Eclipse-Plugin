package com.dubture.composer.ui.wizard.init;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.getcomposer.core.ComposerPackage;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.job.DownloadJob;
import com.dubture.composer.ui.job.InitializeJob;

public class InitComposerWizard extends Wizard implements INewWizard
{
    private InitComposerPage page;
    private final IScriptProject scriptProject;

    public InitComposerWizard(IScriptProject project2)
    {
        this.scriptProject = project2;
    }

    @Override
    public void addPages()
    {
        page = new InitComposerPage(scriptProject);
        addPage(page);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {

    }

    @Override
    public boolean performFinish()
    {
        IResource phar = scriptProject.getProject().findMember("composer.phar");
        
        if (page == null || !(page.getComposerPackage() instanceof ComposerPackage)) {
            return false;
        }
        
        if (phar == null) {
            
            DownloadJob downloadJob = new DownloadJob(scriptProject.getProject(), "Downloading composer.phar");
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
            new InitializeJob(scriptProject.getProject(), page.getComposerPackage()).schedule();
        } catch (FileNotFoundException e) {
            Logger.logException(e);
        }
    }
}
