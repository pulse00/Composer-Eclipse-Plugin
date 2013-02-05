package com.dubture.composer.core.job;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.getcomposer.packagist.PharDownloader;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;

public class DownloadJob extends Job
{
    private IProject project;
    private PharDownloader downloader;

    public DownloadJob(IProject project, String name)
    {
        super(name);
        this.project = project;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            
            monitor.beginTask("Downloading composer.phar from getcomposer.org", 3);
            
            downloader = new PharDownloader();
            InputStream resource = downloader.downloadResource();
            
            monitor.worked(1);
            IFile file = project.getFile("composer.phar");
            monitor.worked(1);
            
            file.create(resource, true, new NullProgressMonitor());
            file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            
            monitor.worked(1);
            
        } catch (Exception e) {
            Logger.logException(e);
            return new Status(Status.ERROR, ComposerPlugin.ID, "Error while downloading composer.phar. See {workspace}/.metadata/.log for details");
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
    
    @Override
    protected void canceling()
    {
        super.canceling();
        downloader.abort();
    }
}
